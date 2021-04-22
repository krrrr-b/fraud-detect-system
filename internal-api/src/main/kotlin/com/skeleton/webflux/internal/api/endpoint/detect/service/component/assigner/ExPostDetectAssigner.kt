package com.skeleton.webflux.internal.api.endpoint.detect.service.component.assigner

import com.skeleton.webflux.common.constant.common.ErrorType
import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.exception.ApiException
import com.skeleton.webflux.common.exception.NotFoundException
import com.skeleton.webflux.data.mongo.detect.FraudDetection
import com.skeleton.webflux.data.mongo.detect.FraudDetectionRepository
import com.skeleton.webflux.internal.api.config.resilience.ResilienceConfiguration
import com.skeleton.webflux.internal.api.endpoint.detect.service.component.assigner.interfaces.Assigner
import com.skeleton.webflux.internal.api.endpoint.detect.service.component.assigner.interfaces.Releaser
import com.skeleton.webflux.internal.api.endpoint.detect.service.component.publisher.items.AfterDetectRegisterEventPublisher
import com.skeleton.webflux.internal.api.endpoint.detect.service.component.publisher.items.AfterDetectReleaseEventPublisher
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ExPostDetectionCommand
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

@Component
class ExPostDetectAssigner(
    @Qualifier(ResilienceConfiguration.MONGO_CIRCUIT_BREAKER)
    private val mongoCircuitBreaker: CircuitBreaker,

    private val afterDetectRegisterEventPublisher: AfterDetectRegisterEventPublisher<FraudDetection>,
    private val afterDetectReleaseEventPublisher: AfterDetectReleaseEventPublisher<FraudDetection>,
    private val fraudDetectionRepository: FraudDetectionRepository,
    private val transactionalOperator: TransactionalOperator,
) : Assigner<ExPostDetectionCommand, FraudDetection>, Releaser {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun assign(userId: Long, command: ExPostDetectionCommand): Mono<FraudDetection> {
        return fraudDetectionRepository
            .findFirstByUserIdAndRuleTypeAndCodeAndActive(userId, command.getRuleType(), command.code, true)
            .filter { it.isActive() }
            .switchIfEmpty(insertDetection(userId, command))
            .`as`(afterDetectRegisterEventPublisher::process)
            .`as`(transactionalOperator::transactional)
            .transformDeferred(CircuitBreakerOperator.of(mongoCircuitBreaker))
            .onErrorResume(CallNotPermittedException::class.java) { Mono.error(ApiException(ErrorType.API_INTERNAL_ERROR)) }
            .onErrorResume(Exception::class.java) {
                logger.error("$this ${it.message} user_id: $userId, command: $command", it)
                Mono.error(ApiException(ErrorType.API_INTERNAL_ERROR))
            }
    }

    private fun insertDetection(
        userId: Long,
        command: ExPostDetectionCommand,
    ): Mono<FraudDetection> {
        return fraudDetectionRepository.save(
            FraudDetection(
                userId = userId,
                category = command.category,
                code = command.code,
                reason = command.reason,
                ruleType = command.getRuleType(),
                active = command.block,
                requireAuthentication = command.requireAuthentication
            )
        )
    }

    override fun release(userId: Long, ruleType: RuleType, code: String): Mono<Boolean> {
        return fraudDetectionRepository
            .findAllByUserIdAndRuleTypeAndCodeAndActive(userId, ruleType, code, true)
            .filter { it.isActive() }
            .switchIfEmpty(Mono.error(NotFoundException(ErrorType.NOT_FOUND.message)))
            .flatMap { fraudDetectionRepository.save(it.close()) }
            .collectList()
            .`as`(afterDetectReleaseEventPublisher::process)
            .`as`(transactionalOperator::transactional)
            .transformDeferred(CircuitBreakerOperator.of(mongoCircuitBreaker))
            .hasElement()
            .onErrorResume(NotFoundException::class.java) { Mono.error(NotFoundException("존재하지 않거나 이미 해지된 차단입니다.")) }
            .onErrorResume(CallNotPermittedException::class.java) { Mono.error(ApiException(ErrorType.API_INTERNAL_ERROR)) }
            .onErrorResume(Exception::class.java) {
                logger.error("$this ${it.message} user_id: $userId, rule_type: $ruleType, code: $code", it)
                Mono.error(it)
            }
    }
}

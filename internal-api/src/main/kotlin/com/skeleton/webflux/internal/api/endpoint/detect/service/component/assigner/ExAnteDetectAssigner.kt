package com.skeleton.webflux.internal.api.endpoint.detect.service.component.assigner

import com.skeleton.webflux.common.constant.common.ErrorType
import com.skeleton.webflux.common.exception.ApiException
import com.skeleton.webflux.common.exception.NotFoundException
import com.skeleton.webflux.data.mongo.detect.ExAnteFraudDetection
import com.skeleton.webflux.data.mongo.detect.ExAnteFraudDetectionRepository
import com.skeleton.webflux.internal.api.config.resilience.ResilienceConfiguration
import com.skeleton.webflux.internal.api.endpoint.detect.service.component.assigner.interfaces.Assigner
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ExAnteDetectionCommand
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
class ExAnteDetectAssigner(
    @Qualifier(ResilienceConfiguration.MONGO_CIRCUIT_BREAKER)
    private val mongoCircuitBreaker: CircuitBreaker,

    private val exAnteFraudDetectionRepository: ExAnteFraudDetectionRepository,
    private val transactionalOperator: TransactionalOperator,
) : Assigner<ExAnteDetectionCommand, ExAnteFraudDetection> {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun assign(userId: Long, command: ExAnteDetectionCommand): Mono<ExAnteFraudDetection> {
        return insertRow(userId, command)
            .`as`(transactionalOperator::transactional)
            .transformDeferred(CircuitBreakerOperator.of(mongoCircuitBreaker))
            .onErrorResume(CallNotPermittedException::class.java) { Mono.error(ApiException(ErrorType.API_INTERNAL_ERROR)) }
            .onErrorResume(Exception::class.java) {
                logger.error("$this ${it.message} user_id: $userId, command: $command", it)
                Mono.error(it)
            }
    }

    private fun insertRow(userId: Long, command: ExAnteDetectionCommand): Mono<ExAnteFraudDetection> {
        return exAnteFraudDetectionRepository.save(
            ExAnteFraudDetection(
                userId = userId,
                category = command.category,
                code = command.code,
                ruleType = command.getRuleType(),
                reason = command.reason,
                target = command.target,
                limitTotalCountForTransaction = command.limitTotalCountForTransaction,
                limitAmountPerTransaction = command.limitAmountPerTransaction,
                limitTotalAmountForTransaction = command.limitTotalAmountForTransaction,
                active = command.block,
                requireAuthentication = command.requireAuthentication,
                expiredAt = command.expiredAt
            )
        )
    }

    fun release(id: String): Mono<Boolean> {
        return exAnteFraudDetectionRepository.findById(id)
            .switchIfEmpty(Mono.error(NotFoundException(ErrorType.NOT_FOUND.message)))
            .flatMap { exAnteFraudDetectionRepository.save(it.close()) }
            .`as`(transactionalOperator::transactional)
            .transformDeferred(CircuitBreakerOperator.of(mongoCircuitBreaker))
            .onErrorResume(CallNotPermittedException::class.java) { Mono.error(ApiException(ErrorType.API_INTERNAL_ERROR)) }
            .onErrorResume(Exception::class.java) {
                logger.error("$this ${it.message} id: $id", it)
                Mono.error(it)
            }
            .thenReturn(true)
    }
}

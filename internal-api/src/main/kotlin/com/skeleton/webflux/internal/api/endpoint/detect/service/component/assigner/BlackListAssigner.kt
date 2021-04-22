package com.skeleton.webflux.internal.api.endpoint.detect.service.component.assigner

import com.skeleton.webflux.common.constant.common.ErrorType
import com.skeleton.webflux.common.exception.ApiException
import com.skeleton.webflux.common.exception.IllegalUserArgumentException
import com.skeleton.webflux.common.exception.NotFoundException
import com.skeleton.webflux.data.mongo.black.BlackList
import com.skeleton.webflux.data.mongo.black.BlackListRepository
import com.skeleton.webflux.internal.api.config.resilience.ResilienceConfiguration
import com.skeleton.webflux.internal.api.endpoint.detect.service.component.assigner.interfaces.Assigner
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ExAnteDetectionCommand
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ReleaseDetectionCommand
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
class BlackListAssigner(
    @Qualifier(ResilienceConfiguration.MONGO_CIRCUIT_BREAKER)
    private val mongoCircuitBreaker: CircuitBreaker,

    private val blackListRepository: BlackListRepository,
    private val transactionalOperator: TransactionalOperator,
) : Assigner<ExAnteDetectionCommand, BlackList> {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun assign(userId: Long, command: ExAnteDetectionCommand): Mono<BlackList> {
        return blackListRepository
            .findByTargetAndActive(command.target ?: mapOf(), true)
            .switchIfEmpty(insertRow(command))
            .`as`(transactionalOperator::transactional)
            .transformDeferred(CircuitBreakerOperator.of(mongoCircuitBreaker))
            .onErrorResume(CallNotPermittedException::class.java) { Mono.error(ApiException(ErrorType.API_INTERNAL_ERROR)) }
            .onErrorResume(Exception::class.java) {
                logger.error("$this ${it.message} user_id: $userId, command: $command", it)
                Mono.error(it)
            }
    }

    fun release(command: ReleaseDetectionCommand): Mono<Boolean> {
        return blackListRepository
            .findByTargetAndActive(command.target ?: mapOf(), true)
            .switchIfEmpty(Mono.error(NotFoundException(ErrorType.NOT_FOUND.message)))
            .flatMap { blackListRepository.save(it.close()) }
            .`as`(transactionalOperator::transactional)
            .transformDeferred(CircuitBreakerOperator.of(mongoCircuitBreaker))
            .onErrorResume(CallNotPermittedException::class.java) { Mono.error(ApiException(ErrorType.API_INTERNAL_ERROR)) }
            .onErrorResume(Exception::class.java) {
                logger.error("$this ${it.message} command: $command", it)
                Mono.error(it)
            }
            .thenReturn(true)
    }

    private fun insertRow(command: ExAnteDetectionCommand): Mono<BlackList> {
        if (null == command.target || command.target.isEmpty()) {
            return Mono.error(IllegalUserArgumentException("잘못된 요청입니다. [target] 탐지 타겟 설정이 필요합니다."))
        }

        return blackListRepository.save(
            BlackList(
                target = command.target,
                active = command.block,
                reason = command.reason,
                expiredAt = command.expiredAt
            )
        )
    }
}

package com.skeleton.webflux.internal.api.endpoint.detect.service.component

import com.skeleton.webflux.common.constant.common.ErrorType
import com.skeleton.webflux.common.exception.ApiException
import com.skeleton.webflux.data.mongo.detect.FraudDetection
import com.skeleton.webflux.data.mongo.detect.FraudDetectionRepository
import com.skeleton.webflux.internal.api.config.resilience.ResilienceConfiguration
import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class DetectionFinder(
    @Qualifier(ResilienceConfiguration.MONGO_CIRCUIT_BREAKER)
    private val mongoCircuitBreaker: CircuitBreaker,
    private val fraudDetectionRepository: FraudDetectionRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun findDetectRuleListByUserId(userId: Long): Flux<FraudDetection> {
        return fraudDetectionRepository
            .findAllByUserIdAndActive(userId, true)
            .transformDeferred(CircuitBreakerOperator.of(mongoCircuitBreaker))
            .onErrorResume(CallNotPermittedException::class.java) { Mono.error(ApiException(ErrorType.API_INTERNAL_ERROR)) }
            .onErrorResume(Exception::class.java) {
                logger.error("$this ${it.message} ${mongoCircuitBreaker.state} user_id: $userId", it)
                Mono.error(it)
            }
    }

    fun findDetectBlockStatusByUserId(userId: Long): Mono<VerifiedResultResource> {
        return fraudDetectionRepository
            .findAllByUserIdAndActive(userId, true)
            .next()
            .map { VerifiedResultResource.detect() }
            .switchIfEmpty(Mono.just(VerifiedResultResource.ok()))
            .transformDeferred(CircuitBreakerOperator.of(mongoCircuitBreaker))
            .doOnError { logger.error("$this ${it.message} ${mongoCircuitBreaker.state} user_id: $userId", it) }
            .onErrorResume(CallNotPermittedException::class.java) { Mono.error(ApiException(ErrorType.API_INTERNAL_ERROR)) }
            .onErrorResume(Exception::class.java) { Mono.just(VerifiedResultResource.ok()) }
    }
}

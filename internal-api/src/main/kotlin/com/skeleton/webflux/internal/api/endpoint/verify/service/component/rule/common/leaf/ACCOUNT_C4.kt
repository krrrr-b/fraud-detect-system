package com.skeleton.webflux.internal.api.endpoint.verify.service.component.rule.common.leaf

import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.constant.verify.VerifyResult
import com.skeleton.webflux.data.mongo.detect.FraudDetectionRepository
import com.skeleton.webflux.internal.api.config.resilience.ResilienceConfiguration.Companion.MONGO_CIRCUIT_BREAKER
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.interfaces.Rule
import com.skeleton.webflux.internal.api.endpoint.verify.spec.query.TransactionQuery
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ACCOUNT_C4(
    @Qualifier(MONGO_CIRCUIT_BREAKER)
    private val mongoCircuitBreaker: CircuitBreaker,
    private val fraudDetectionRepository: FraudDetectionRepository,
) : Rule<TransactionQuery>(
    RuleType.ACCOUNT_C4,
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun isVerified(userId: Long, query: TransactionQuery): Mono<VerifyResult> {
        logger.debug("이상금융거래_탐지_리스트_체크")

        return fraudDetectionRepository
            .findFirstByUserIdAndActive(userId, true)
            .filter { it.isActive() }
            .map { VerifyResult.DETECT }
            .switchIfEmpty(Mono.just(VerifyResult.PASS))
            .transformDeferred(CircuitBreakerOperator.of(mongoCircuitBreaker))
            .onErrorResume(CallNotPermittedException::class.java) { Mono.just(VerifyResult.PASS) }
            .onErrorResume(Exception::class.java) {
                logger.error("$this ${it.message} user_id: $userId, query: $query", it)
                Mono.just(VerifyResult.PASS)
            }
    }
}

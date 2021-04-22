package com.skeleton.webflux.internal.api.endpoint.verify.service.component.rule.common.leaf

import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.constant.verify.TargetKey
import com.skeleton.webflux.common.constant.verify.VerifyResult
import com.skeleton.webflux.data.mongo.black.BlackList
import com.skeleton.webflux.internal.api.config.resilience.ResilienceConfiguration.Companion.MONGO_CIRCUIT_BREAKER
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.interfaces.Rule
import com.skeleton.webflux.internal.api.endpoint.verify.spec.query.TransactionQuery
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ACCOUNT_C3(
    @Qualifier(MONGO_CIRCUIT_BREAKER)
    private val mongoCircuitBreaker: CircuitBreaker,
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) : Rule<TransactionQuery>(
    RuleType.ACCOUNT_C3
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun isVerified(userId: Long, query: TransactionQuery): Mono<VerifyResult> {
        logger.debug("블랙리스트_체크")

        return reactiveMongoTemplate
            .find(createMongoQuery(userId, query), BlackList::class.java)
            .collectList() // @todo. 존재유무만 확인하기 위해서는 .hasElements() 로 변경
            .filter { it.isNotEmpty() }
            .map { VerifyResult.DETECT }
            .doOnNext { logger.info("블랙 리스트 대상 트랜잭션 탐지 $it") }
            .switchIfEmpty(Mono.just(VerifyResult.PASS))
            .transformDeferred(CircuitBreakerOperator.of(mongoCircuitBreaker))
            .onErrorResume(CallNotPermittedException::class.java) { Mono.just(VerifyResult.PASS) }
            .onErrorResume(Exception::class.java) {
                logger.error("$this ${it.message} user_id: $userId, query: $query", it)
                Mono.just(VerifyResult.PASS)
            }
    }

    private fun createMongoQuery(userId: Long, query: TransactionQuery) =
        Query(Criteria.where("target.${TargetKey.user_id}")
            .isEqualTo(userId)
            .orOperator(
                Criteria.where("target.${TargetKey.user_name}")
                    .isEqualTo(query.target()[TargetKey.user_name]),

                Criteria.where("target.${TargetKey.bank_code}")
                    .isEqualTo(query.target()[TargetKey.bank_code]),

                Criteria.where("target.${TargetKey.account_number}")
                    .isEqualTo(query.target()[TargetKey.account_number]),

                Criteria.where("target.${TargetKey.card_number}")
                    .isEqualTo(query.target()[TargetKey.card_number]),

                Criteria.where("target.${TargetKey.merchant_category_code}")
                    .isEqualTo(query.target()[TargetKey.merchant_category_code])
            )
        )
}

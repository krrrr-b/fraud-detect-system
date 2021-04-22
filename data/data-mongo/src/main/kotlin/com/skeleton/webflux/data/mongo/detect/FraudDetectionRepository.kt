package com.skeleton.webflux.data.mongo.detect

import com.skeleton.webflux.common.constant.verify.RuleType
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface FraudDetectionRepository : ReactiveMongoRepository<FraudDetection, String> {
    fun findAllByUserIdAndRuleTypeAndCodeAndActive(
        userId: Long,
        ruleType: RuleType,
        code: String,
        active: Boolean
    ): Flux<FraudDetection>

    fun findFirstByUserIdAndActive(
        userId: Long,
        active: Boolean
    ): Mono<FraudDetection>

    fun findAllByUserIdAndActive(
        userId: Long,
        active: Boolean
    ): Flux<FraudDetection>

    fun findFirstByUserIdAndRuleTypeAndCodeAndActive(
        userId: Long,
        ruleType: RuleType,
        code: String,
        active: Boolean
    ): Mono<FraudDetection>
}

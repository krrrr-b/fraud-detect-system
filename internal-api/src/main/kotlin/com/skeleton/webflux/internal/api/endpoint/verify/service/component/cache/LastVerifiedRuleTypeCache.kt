package com.skeleton.webflux.internal.api.endpoint.verify.service.component.cache

import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.data.redis.component.ReactiveRedisExpireCache
import com.skeleton.webflux.internal.api.config.resilience.ResilienceConfiguration
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class LastVerifiedRuleTypeCache(
    @Qualifier(ResilienceConfiguration.REDIS_CIRCUIT_BREAKER)
    private val redisCircuitBreaker: CircuitBreaker,

    private val redisExpireCache: ReactiveRedisExpireCache,
) {
    fun get(userId: Long): Mono<RuleType> {
        return redisExpireCache.getObject(getLastVerifiedRuleTypeKey(userId))
            .map { RuleType.findRuleByName(it) }
            .transformDeferred(CircuitBreakerOperator.of(redisCircuitBreaker))
    }

    fun sink(userId: Long, ruleType: RuleType): Mono<Boolean> {
        return redisExpireCache.set(getLastVerifiedRuleTypeKey(userId), ruleType.name, Duration.ofDays(2))
            .transformDeferred(CircuitBreakerOperator.of(redisCircuitBreaker))
    }

    fun release(userId: Long): Mono<Boolean> {
        return redisExpireCache.delete(getLastVerifiedRuleTypeKey(userId))
            .transformDeferred(CircuitBreakerOperator.of(redisCircuitBreaker))
    }

    private fun getLastVerifiedRuleTypeKey(userId: Long): String {
        return "key"
    }
}

package com.skeleton.webflux.internal.api.endpoint.verify.service.component.verifier

import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.exception.NotFoundException
import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.cache.LastVerifiedRuleTypeCache
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.interfaces.Verifier
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.publisher.ExAnteEventPublisher
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.rule.RuleConverter
import com.skeleton.webflux.internal.api.endpoint.verify.spec.resource.RuleForestResource
import io.lettuce.core.RedisException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ExAnteVerifier(
    private val lastVerifiedRuleTypeCache: LastVerifiedRuleTypeCache,
    private val exAnteEventPublisher: ExAnteEventPublisher,
    private val ruleForestResource: RuleForestResource,
    private val ruleConverter: RuleConverter,
) : Verifier(ruleForestResource, ruleConverter) {
    override fun execute(): Mono<VerifiedResultResource> {
        return verify(ruleForestResource.findLeafRules(transactionFlow))

            // @description 검증 후처리를 진행한다
            .doOnNext { afterProcess(it).subscribe() }
            .map { VerifiedResultResource.by(it) }
    }

    private fun afterProcess(ruleType: RuleType) =
        exAnteEventPublisher.publish(ruleType, this)

    private fun verify(ruleTypes: List<RuleType>): Mono<RuleType> {
        return lastVerifiedRuleTypeCache.get(userId)
            .onErrorResume(NotFoundException::class.java) {
                verifyLeafRules(ruleTypes).next()
                    .defaultIfEmpty(RuleType.PASS)
                    .doOnNext { lastVerifiedRuleTypeCache.sink(userId, it).subscribe() }
            }
            .onErrorResume(RedisException::class.java) {
                verifyLeafRules(ruleTypes).next()
                    .defaultIfEmpty(RuleType.PASS)
            }
    }
}

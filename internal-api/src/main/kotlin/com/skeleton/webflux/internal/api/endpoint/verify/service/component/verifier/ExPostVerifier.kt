package com.skeleton.webflux.internal.api.endpoint.verify.service.component.verifier

import com.skeleton.webflux.common.constant.verify.RuleStep
import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.interfaces.Verifier
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.publisher.ExPostEventPublisher
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.rule.RuleConverter
import com.skeleton.webflux.internal.api.endpoint.verify.spec.resource.RuleForestResource
import com.skeleton.webflux.internal.api.endpoint.verify.spec.resource.merge
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.stream.Collectors

@Component
class ExPostVerifier(
    private val exPostEventPublisher: ExPostEventPublisher,
    private val ruleForestResource: RuleForestResource,
    private val ruleConverter: RuleConverter,
) : Verifier(ruleForestResource, ruleConverter) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val DEFAULT_PERCENT_SCORE = 100
        const val DEFAULT_LIMIT_SCORE = 100
    }

    override fun execute(): Mono<VerifiedResultResource> {
        val startAt = LocalDateTime.now()
        return mergedRootAndBranchRules()
            .zipWhen { verifyLeafRules(it).collectList() }
            .map { it ->
                // @description 잎사귀 룰 합산 점수
                val summaryScore = it.t2.stream()
                    .mapToInt { ruleForestResource.findScore(it) }
                    .sum()

                // @description 뿌리, 가지 룰 가산율
                val additionalRate = it.t1.stream()
                    .mapToInt { ruleForestResource.findScore(it) }
                    .sum()
                    .also { (it + DEFAULT_PERCENT_SCORE) / DEFAULT_PERCENT_SCORE }

                if (isOverScore(summaryScore, additionalRate)) {
                    // @description 검증 후처리를 진행한다
                    afterProcess(it.t2).subscribe()

                    return@map RuleType.DETECT
                }

                RuleType.PASS
            }
            // .elapsed()
            .map { VerifiedResultResource.by(it) }
    }

    private fun isOverScore(summaryScore: Int, additionalRate: Int): Boolean {
        return (summaryScore + (summaryScore * additionalRate)) >= DEFAULT_LIMIT_SCORE
    }

    private fun afterProcess(leafRules: MutableList<RuleType>) =
        exPostEventPublisher.publish(leafRules, this)

    // @description 뿌리 룰과 가지 룰의 리스트를 합친다
    private fun mergedRootAndBranchRules(): Mono<MutableList<RuleType>> {
        return findRootRules()
            .zipWhen { rootRules -> findBranchRules(rootRules) }
            .map { merge(it.t1, it.t2).toMutableList() }
    }

    private fun findRootRules() =
        verifyRules(RuleStep.ROOT, ruleForestResource.findRootRules(transactionFlow))
            .collectList()

    private fun findBranchRules(rootRules: MutableList<RuleType>) =
        verifyRules(RuleStep.BRANCH, ruleForestResource.findBranchRules(transactionFlow), rootRules)
            .collect(Collectors.toList())
}

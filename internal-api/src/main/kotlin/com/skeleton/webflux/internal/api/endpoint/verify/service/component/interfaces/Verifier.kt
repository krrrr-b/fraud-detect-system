package com.skeleton.webflux.internal.api.endpoint.verify.service.component.interfaces

import com.skeleton.webflux.common.constant.transaction.TransactionFlow
import com.skeleton.webflux.common.constant.verify.RuleStep
import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import com.skeleton.webflux.internal.api.endpoint.verify.spec.query.TransactionQuery
import com.skeleton.webflux.internal.api.endpoint.verify.spec.resource.RuleForestResource
import com.skeleton.webflux.internal.api.endpoint.verify.spec.resource.SituationRules
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.rule.RuleConverter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

abstract class Verifier(
    private val ruleForestResource: RuleForestResource,
    private val ruleConverter: RuleConverter,
) {
    lateinit var transactionFlow: TransactionFlow
    lateinit var situationRules: SituationRules
    lateinit var query: TransactionQuery
    var userId: Long = 0L

    fun setUserParameters(
        query: TransactionQuery,
        userId: Long,
        transactionFlow: TransactionFlow,
    ): Verifier {
        this.transactionFlow = transactionFlow
        this.userId = userId
        this.query = query

        return this
    }

    fun setRules(situationRules: SituationRules): Verifier {
        this.situationRules = situationRules

        return this
    }

    protected fun isVerified(
        rule: Rule<TransactionQuery>,
        step: RuleStep,
        precedeRules: MutableList<RuleType>,
    ): Mono<Boolean> {
        if (step.isRootOrBranchRule()) {
            return rule.isVerified(userId, query)
                .map { it.isDetect() }
        }

        // @description 선행 조건이 완료되어야 한다
        return ruleForestResource.isPrecededRules(rule.getRuleType(), precedeRules)
            .flatMap {
                return@flatMap rule.isVerified(userId, query)
                    .map { it.isDetect() }
            }
    }

    protected fun verifyRules(
        step: RuleStep,
        rules: List<RuleType>,
        precedeRules: MutableList<RuleType> = mutableListOf()
    ) = Flux.fromStream(rules.stream())
        .filterWhen { isVerified(ruleConverter.convert(it), step, precedeRules) }

    // @description 잎사귀 룰을 검증해서 실패한 첫번째 룰을 가져온다
    protected fun verifyLeafRules(
        leafRules: List<RuleType>,
        rootAndBranchRules: MutableList<RuleType> = mutableListOf()
    ) = verifyRules(RuleStep.LEAF, leafRules, rootAndBranchRules)

    abstract fun execute(): Mono<VerifiedResultResource>
}

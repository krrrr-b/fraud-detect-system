package com.skeleton.webflux.internal.api.endpoint.verify.spec.resource

import com.skeleton.webflux.common.constant.common.ErrorType
import com.skeleton.webflux.common.constant.transaction.TransactionFlow
import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.exception.ApiException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RuleForestResource(
    var situationRules: Map<TransactionFlow, SituationRules> = mapOf(),
) {
    fun isPrecededRules(
        ruleType: RuleType,
        requiredRules: List<RuleType>
    ): Mono<Boolean> {
        if (requiredRules.isEmpty()) {
            return Mono.just(true)
        }

        // @todo. 선행 룰셋 체크
        return Mono.just(true)
    }

    fun findScore(ruleType: RuleType): Int {
        return 10 // @todo. score
    }

    fun findRulesByTransactionFlow(transactionFlow: TransactionFlow): SituationRules {
        return situationRules.getOrDefault(transactionFlow, null)
            ?: throw ApiException(ErrorType.API_INTERNAL_ERROR)
    }

    fun findLeafRules(transactionFlow: TransactionFlow): List<RuleType> {
        return situationRules.getOrDefault(transactionFlow, null)?.leafRules
            ?: throw ApiException(ErrorType.API_INTERNAL_ERROR)
    }

    fun findBranchRules(transactionFlow: TransactionFlow): List<RuleType> {
        return situationRules.getOrDefault(transactionFlow, null)?.branchRules
            ?: throw ApiException(ErrorType.API_INTERNAL_ERROR)
    }

    fun findRootRules(transactionFlow: TransactionFlow): List<RuleType> {
        return situationRules.getOrDefault(transactionFlow, null)?.rootRules
            ?: throw ApiException(ErrorType.API_INTERNAL_ERROR)
    }
}

fun <T> merge(first: List<T>, second: List<T>): List<T> {
    return first + second
}

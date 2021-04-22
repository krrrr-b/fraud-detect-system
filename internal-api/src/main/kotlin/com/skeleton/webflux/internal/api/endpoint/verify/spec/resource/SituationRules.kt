package com.skeleton.webflux.internal.api.endpoint.verify.spec.resource

import com.skeleton.webflux.common.constant.transaction.TransactionFlow
import com.skeleton.webflux.common.constant.verify.RuleType

data class SituationRules(
    var transactionFlow: TransactionFlow,
    var rootRules: List<RuleType>,
    var branchRules: List<RuleType>,
    var leafRules: List<RuleType>,
) {
    fun totalRuleCount(): Int {
        return rootRules.size + branchRules.size + leafRules.size
    }
}

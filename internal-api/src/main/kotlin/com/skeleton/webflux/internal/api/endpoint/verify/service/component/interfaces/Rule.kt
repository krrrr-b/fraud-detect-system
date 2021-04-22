package com.skeleton.webflux.internal.api.endpoint.verify.service.component.interfaces

import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.constant.verify.VerifyResult
import com.skeleton.webflux.internal.api.endpoint.verify.spec.query.TransactionQuery
import reactor.core.publisher.Mono

abstract class Rule<T>(
    private var ruleType: RuleType,
) where T : TransactionQuery {
    open fun getRuleType(): RuleType = ruleType

    abstract fun isVerified(userId: Long, query: T): Mono<VerifyResult>
}

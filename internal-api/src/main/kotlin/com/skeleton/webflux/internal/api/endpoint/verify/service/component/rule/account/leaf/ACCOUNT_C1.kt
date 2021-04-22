package com.skeleton.webflux.internal.api.endpoint.verify.service.component.rule.account.leaf

import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.constant.verify.VerifyResult
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.interfaces.Rule
import com.skeleton.webflux.internal.api.endpoint.verify.spec.query.TransactionQuery
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ACCOUNT_C1(
) : Rule<TransactionQuery>(
    RuleType.ACCOUNT_C1,
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun isVerified(userId: Long, query: TransactionQuery): Mono<VerifyResult> {
        logger.debug("조건 작성")

        return Mono.just(VerifyResult.PASS)
    }
}

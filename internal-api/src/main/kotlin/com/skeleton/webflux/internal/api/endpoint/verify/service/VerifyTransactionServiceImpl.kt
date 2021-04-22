package com.skeleton.webflux.internal.api.endpoint.verify.service

import com.skeleton.webflux.common.constant.transaction.TransactionFlow
import com.skeleton.webflux.internal.api.endpoint.verify.spec.query.TransactionQuery
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.RulePresenter
import org.springframework.stereotype.Service

@Service
class VerifyTransactionServiceImpl(
    private val rulePresenter: RulePresenter,
) : VerifyTransactionService {
    override fun verify(transactionFlow: TransactionFlow, query: TransactionQuery) =
        rulePresenter.present(transactionFlow, query)
}

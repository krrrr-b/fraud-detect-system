package com.skeleton.webflux.internal.api.endpoint.verify.service.component

import com.skeleton.webflux.common.constant.transaction.TransactionFlow
import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import com.skeleton.webflux.internal.api.endpoint.verify.spec.query.TransactionQuery
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.verifier.ExAnteVerifier
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.verifier.ExPostVerifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RulePresenter(
    private val exAnteVerifier: ExAnteVerifier,
    private val exPostVerifier: ExPostVerifier,
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun present(
        transactionFlow: TransactionFlow,
        query: TransactionQuery,
    ): Mono<VerifiedResultResource> {
        return findVerifierByTransactionFlow(query.userId, transactionFlow, query)
            .doOnError { logger.error("$this transaction_flow: $transactionFlow, query: $query, ${it.message}", it) }
            .onErrorReturn(VerifiedResultResource.ok())
    }

    private fun findVerifierByTransactionFlow(
        userId: Long,
        transactionFlow: TransactionFlow,
        query: TransactionQuery,
    ) = findVerifierByTransactionFlow(transactionFlow)
        .setUserParameters(query, userId, transactionFlow)
        .execute()

    private fun findVerifierByTransactionFlow(transactionFlow: TransactionFlow) =
        if (transactionFlow.isExAnte()) exAnteVerifier else exPostVerifier
}

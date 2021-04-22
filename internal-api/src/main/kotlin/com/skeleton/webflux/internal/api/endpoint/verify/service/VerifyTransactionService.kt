package com.skeleton.webflux.internal.api.endpoint.verify.service

import com.skeleton.webflux.common.constant.transaction.TransactionFlow
import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import com.skeleton.webflux.internal.api.endpoint.verify.spec.query.TransactionQuery
import reactor.core.publisher.Mono

interface VerifyTransactionService {
    fun verify(transactionFlow: TransactionFlow, query: TransactionQuery): Mono<VerifiedResultResource>
}

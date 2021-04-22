package com.skeleton.webflux.internal.api.endpoint.verify.spec

import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import com.skeleton.webflux.internal.api.endpoint.verify.spec.query.TransactionQuery
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import reactor.core.publisher.Mono

@Tag(name = "사전 트랜잭션 검증")
interface ExAnteVerifyTransactionControllerSpec {
    @Operation(summary = "트랜잭션 검증")
    fun verifyTransaction(query: TransactionQuery): Mono<VerifiedResultResource>
}

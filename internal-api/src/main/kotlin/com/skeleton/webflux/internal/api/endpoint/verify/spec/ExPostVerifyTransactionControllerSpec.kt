package com.skeleton.webflux.internal.api.endpoint.verify.spec

import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import com.skeleton.webflux.internal.api.endpoint.verify.spec.query.TransactionQuery
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import reactor.core.publisher.Mono

@Tag(name = "사후 트랜잭션 검증")
interface ExPostVerifyTransactionControllerSpec {
    @Operation(summary = "트랜잭션 사후 검증")
    fun exPostVerifyTransaction(query: TransactionQuery): Mono<VerifiedResultResource>
}

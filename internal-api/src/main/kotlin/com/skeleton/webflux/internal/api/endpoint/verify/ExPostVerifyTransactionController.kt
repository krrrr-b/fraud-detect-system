package com.skeleton.webflux.internal.api.endpoint.verify

import com.skeleton.webflux.common.constant.transaction.TransactionFlow
import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import com.skeleton.webflux.internal.api.endpoint.verify.service.VerifyTransactionService
import com.skeleton.webflux.internal.api.endpoint.verify.spec.ExPostVerifyTransactionControllerSpec
import com.skeleton.webflux.internal.api.endpoint.verify.spec.query.TransactionQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

const val REQUEST_PATH_AFTER_VERIFY = "/api-internal/v1/ex-post-verify"

@RestController
@RequestMapping(value = [REQUEST_PATH_AFTER_VERIFY])
class ExPostVerifyTransactionController @Autowired constructor(
    private val verifyTransactionService: VerifyTransactionService
) : ExPostVerifyTransactionControllerSpec {

    @PostMapping(REQUEST_PATH_TRANSACTION)
    override fun exPostVerifyTransaction(
        @Valid @RequestBody query: TransactionQuery
    ): Mono<VerifiedResultResource> {
        return verifyTransactionService.verify(TransactionFlow.ex_post_withdraw, query)
    }
}

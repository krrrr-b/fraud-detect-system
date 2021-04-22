package com.skeleton.webflux.internal.api.endpoint.verify

import com.skeleton.webflux.common.constant.transaction.TransactionFlow
import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import com.skeleton.webflux.internal.api.endpoint.verify.service.VerifyTransactionService
import com.skeleton.webflux.internal.api.endpoint.verify.spec.ExAnteVerifyTransactionControllerSpec
import com.skeleton.webflux.internal.api.endpoint.verify.spec.query.TransactionQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.validation.Valid

const val REQUEST_PATH_VERIFY = "/api-internal/v1/verify"
const val REQUEST_PATH_TRANSACTION = "/transaction"

@RestController
@RequestMapping(path = [REQUEST_PATH_VERIFY])
class ExAnteVerifyTransactionController @Autowired constructor(
    private val verifyTransactionService: VerifyTransactionService,
) : ExAnteVerifyTransactionControllerSpec {

    @PostMapping(REQUEST_PATH_TRANSACTION)
    override fun verifyTransaction(
        @Valid @RequestBody query: TransactionQuery,
    ): Mono<VerifiedResultResource> {
        return verifyTransactionService.verify(TransactionFlow.withdraw, query)
    }
}

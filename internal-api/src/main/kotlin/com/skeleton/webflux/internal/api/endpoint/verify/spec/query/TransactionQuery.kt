package com.skeleton.webflux.internal.api.endpoint.verify.spec.query

import com.fasterxml.jackson.annotation.JsonIgnore
import com.skeleton.webflux.common.constant.verify.TargetKey
import java.math.BigDecimal

data class TransactionQuery(
    val userId: Long,
    val transactionAmount: BigDecimal,
    val accountNumber: String,
    val bankCode: String
) {
    @JsonIgnore
    fun target(): Map<TargetKey, String> {
        val target = mutableMapOf<TargetKey, String>()
        target[TargetKey.account_number] = accountNumber
        target[TargetKey.bank_code] = bankCode

        return target.toMap()
    }
}

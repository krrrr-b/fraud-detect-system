package com.skeleton.webflux.data.mongo.detect

import com.skeleton.webflux.common.constant.verify.AfterProcessType
import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.constant.verify.TargetKey
import com.skeleton.webflux.data.mongo.BaseTimeDocument
import org.apache.logging.log4j.util.Strings
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@Document("ex_ante_fraud_detection")
data class ExAnteFraudDetection @PersistenceConstructor
constructor(
    @Field("user_id")
    val userId: Long,

    @Field
    val category: String,

    @Field
    val code: String,

    @Field("rule_type")
    val ruleType: RuleType,

    @Field
    val reason: String = Strings.EMPTY,

    @Field
    val target: Map<TargetKey, String>?,

    @Field("limit_total_count_for_transaction")
    val limitTotalCountForTransaction: Int?,

    @Field("limit_amount_per_transaction")
    val limitAmountPerTransaction: BigDecimal?,

    @Field("limit_total_amount_for_transaction")
    val limitTotalAmountForTransaction: BigDecimal?,

    @Field
    var active: Boolean = false,

    @Field("require_authentication")
    val requireAuthentication: AfterProcessType,

    @Field("expired_at")
    var expiredAt: LocalDateTime = LocalDate.now(ZoneId.systemDefault())
        .plusDays(9999)
        .atStartOfDay(),
) : BaseTimeDocument() {
    fun isActive(): Boolean {
        return active && expiredAt.isAfter(LocalDateTime.now())
    }

    fun close(): ExAnteFraudDetection {
        active = false
        return this
    }

    fun open(): ExAnteFraudDetection {
        active = true
        return this
    }
}

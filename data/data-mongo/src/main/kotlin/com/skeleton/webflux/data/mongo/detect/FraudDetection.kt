package com.skeleton.webflux.data.mongo.detect

import com.skeleton.webflux.common.constant.verify.AfterProcessType
import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.data.mongo.BaseTimeDocument
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@Document("fraud_detection")
data class FraudDetection @PersistenceConstructor
constructor(
    @Field("user_id")
    val userId: Long,

    @Field
    val category: String,

    @Field
    val code: String,

    @Field
    val reason: String,

    @Field("rule_type")
    val ruleType: RuleType,

    @Field("is_active")
    var active: Boolean,

    @Field("require_authentication")
    val requireAuthentication: AfterProcessType,

    @Field("expired_at")
    var expiredAt: LocalDateTime = LocalDate.now(ZoneId.systemDefault())
        .plusDays(9999)
        .atStartOfDay(),
) : BaseTimeDocument() {
    fun isActive() = active && expiredAt.isAfter(LocalDateTime.now())
    fun close(): FraudDetection {
        active = false
        return this
    }
}

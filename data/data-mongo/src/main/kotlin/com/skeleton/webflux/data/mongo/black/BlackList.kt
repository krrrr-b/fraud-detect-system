package com.skeleton.webflux.data.mongo.black

import com.skeleton.webflux.common.constant.verify.TargetKey
import com.skeleton.webflux.data.mongo.BaseTimeDocument
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

const val BLACK_LIST_DATABASE = "black_list"

@Document(BLACK_LIST_DATABASE)
data class BlackList @PersistenceConstructor
constructor(
    @Field("target")
    val target: Map<TargetKey, String>,

    @Field("is_active")
    var active: Boolean = true,

    @Field
    var reason: String? = null,

    @Field("expired_at")
    val expiredAt: LocalDateTime? = LocalDate.now(ZoneId.systemDefault())
        .plusDays(9999)
        .atStartOfDay()
) : BaseTimeDocument() {
    fun close(): BlackList {
        active = false
        return this
    }
}

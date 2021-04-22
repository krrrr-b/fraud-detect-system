package com.skeleton.webflux.internal.api.endpoint.detect.spec.command

import com.fasterxml.jackson.annotation.JsonIgnore
import com.skeleton.webflux.common.constant.verify.AfterProcessType
import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.constant.verify.TargetKey
import io.swagger.v3.oas.annotations.media.Schema
import org.apache.logging.log4j.util.Strings
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.validation.constraints.NotEmpty

data class ExAnteDetectionCommand(
    @field:NotEmpty
    val category: String,

    @field:NotEmpty
    val code: String,

    @field:Schema(title = "탐지 사유")
    val reason: String = Strings.EMPTY,

    @field:Schema(title = "탐지 타겟")
    val target: Map<TargetKey, String>? = mapOf(),

    @field:Schema(title = "횟수 제한")
    val limitTotalCountForTransaction: Int?,

    @field:Schema(title = "트랜잭션 금액 제한")
    val limitAmountPerTransaction: BigDecimal?,

    @field:Schema(title = "사용금액 제한")
    val limitTotalAmountForTransaction: BigDecimal?,

    @field:Schema(title = "차단 여부")
    val block: Boolean = false,

    @field:Schema(title = "차단 해지를 위한 인증 방법")
    val requireAuthentication: AfterProcessType,

    @field:Schema(title = "모니터링 만료 일시")
    val expiredAt: LocalDateTime = LocalDateTime.now().plusDays(5),
) {
    @JsonIgnore
    fun getRuleType() = RuleType.findRuleByName(code)
}

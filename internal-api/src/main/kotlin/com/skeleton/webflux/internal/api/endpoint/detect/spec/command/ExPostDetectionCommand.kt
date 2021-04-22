package com.skeleton.webflux.internal.api.endpoint.detect.spec.command

import com.fasterxml.jackson.annotation.JsonIgnore
import com.skeleton.webflux.common.constant.verify.AfterProcessType
import com.skeleton.webflux.common.constant.verify.RuleType
import io.swagger.v3.oas.annotations.media.Schema
import org.apache.logging.log4j.util.Strings
import javax.validation.constraints.NotEmpty

data class ExPostDetectionCommand(
    @field:NotEmpty
    val category: String,

    @field:NotEmpty
    val code: String,

    @field:Schema(title = "탐지 사유")
    val reason: String = Strings.EMPTY,

    @field:Schema(title = "차단 여부")
    val block: Boolean = false,

    @field:Schema(title = "차단 해지를 위한 인증 방법")
    val requireAuthentication: AfterProcessType,
) {
    @JsonIgnore
    fun getRuleType() = RuleType.findRuleByName(code)
}

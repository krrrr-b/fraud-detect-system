package com.skeleton.webflux.internal.api.endpoint.detect.spec.command

import com.fasterxml.jackson.annotation.JsonIgnore
import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.constant.verify.TargetKey
import io.swagger.v3.oas.annotations.media.Schema
import org.apache.logging.log4j.util.Strings
import javax.validation.constraints.NotEmpty

data class ReleaseDetectionCommand(
    @field:NotEmpty
    @field:Schema(title = "탐지 카테고리")
    val category: String,

    @field:NotEmpty
    @field:Schema(title = "탐지 코드")
    val code: String,

    @field:Schema(title = "탐지 사유")
    val reason: String = Strings.EMPTY,

    @field:Schema(title = "탐지 타겟")
    val target: Map<TargetKey, String>? = mapOf(),
) {
    @JsonIgnore
    fun getRuleType() = RuleType.findRuleByName(code)
}

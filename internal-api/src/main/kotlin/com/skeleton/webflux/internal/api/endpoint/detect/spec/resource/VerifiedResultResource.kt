package com.skeleton.webflux.internal.api.endpoint.detect.spec.resource

import com.skeleton.webflux.common.constant.common.ErrorType
import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.constant.verify.VerifiedResultType
import java.lang.IllegalArgumentException

data class VerifiedResultResource(
    val result: VerifiedResult,
    val isSucceed: Boolean = false,
) {
    companion object {
        fun ok() = VerifiedResultResource(VerifiedResult(VerifiedResultType.OK, RuleType.PASS), true)
        fun detect() = VerifiedResultResource(VerifiedResult(VerifiedResultType.DETECTION, RuleType.DETECT))
        fun error() = VerifiedResultResource(VerifiedResult(VerifiedResultType.ERROR, RuleType.ERROR))

        fun by(ruleType: RuleType): VerifiedResultResource {
            return when (ruleType.resultType) {
                VerifiedResultType.OK ->
                    VerifiedResultResource(VerifiedResult(VerifiedResultType.OK, ruleType), true)
                VerifiedResultType.DETECTION ->
                    VerifiedResultResource(VerifiedResult(VerifiedResultType.DETECTION, ruleType))
                VerifiedResultType.ERROR ->
                    VerifiedResultResource(VerifiedResult(VerifiedResultType.ERROR, ruleType))

                else -> throw IllegalArgumentException(ErrorType.BAD_REQUEST.message)
            }
        }
    }
}

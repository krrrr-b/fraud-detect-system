package com.skeleton.webflux.internal.api.endpoint.detect.spec.resource

import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.constant.verify.VerifiedResultType

data class VerifiedResult(
    var code: VerifiedResultType = VerifiedResultType.OK,
    var message: String = VerifiedResultType.OK.message,
    var pattern: String = VerifiedResultType.OK.pattern,
) {
    constructor(result: VerifiedResultType, ruleType: RuleType) : this() {
        this.code = result
        this.message = ruleType.resultType.message
        this.pattern = ruleType.name
    }
}

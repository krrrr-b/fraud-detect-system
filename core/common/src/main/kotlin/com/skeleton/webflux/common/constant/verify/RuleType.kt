package com.skeleton.webflux.common.constant.verify

enum class RuleType(
    val resultType: VerifiedResultType = VerifiedResultType.DETECTION,
) {
    // @description 기본 룰셋
    PASS(VerifiedResultType.OK),
    DETECT,
    ERROR(VerifiedResultType.ERROR),

    // @description 계정 룰셋
    ACCOUNT_A1,
    ACCOUNT_A2,

    ACCOUNT_B1,
    ACCOUNT_B2,

    ACCOUNT_C1,
    ACCOUNT_C2,
    ACCOUNT_C3,
    ACCOUNT_C4
    ;

    companion object {
        fun findRuleByName(name: String): RuleType {
            return values()
                .firstOrNull { it.name == name }
                .let { it ?: DETECT }
        }
    }
}

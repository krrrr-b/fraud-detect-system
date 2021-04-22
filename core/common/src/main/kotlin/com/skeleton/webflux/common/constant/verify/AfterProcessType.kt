package com.skeleton.webflux.common.constant.verify

enum class AfterProcessType(
    var description: String,
    var orderPoint: Int,
    var ruleTypes: List<RuleType>,
    val isRequireAuthentication: Boolean = false
) {
    NOTIFICATION_OF_PERSON_IN_CHARGE(
        "담당자 통보",
        100,
        listOf(
            RuleType.ACCOUNT_C1,
            RuleType.ACCOUNT_C2,
        )
    ),

    NOTIFICATION_FINANCIAL_INSTITUTION(
        "금융기관 공유",
        99,
        emptyList()
    ),

    REGISTRATION_BLACK_LIST_ACCOUNT(
        "사용자 블랙리스트 등록",
        4,
        emptyList(),
        true
    ),

    REGISTRATION_BORROW_BANK_ACCOUNT(
        "대포통장 사용자 등록",
        3,
        emptyList()
    ),

    REGISTRATION_SUSPENSION_CODE(
        "수신주의 코드 지급정지 등록",
        2,
        emptyList(),
        true
    ),

    REQUIRE_TWO_FACTOR_AUTHENTICATION(
        "2차 인증 요청 (셀피)",
        1,
        listOf(
            RuleType.ACCOUNT_C3,
            RuleType.ACCOUNT_C4,
        ),
        true
    ),

    REQUIRE_CH_PHONE_CALL_AUTHENTICATION(
        "2차 인증 요청 (영상통화)",
        0,
        emptyList(),
        true
    );

    companion object {
        fun findByName(name: String): AfterProcessType? {
            return values().firstOrNull { it.name == name }
        }

        fun findAfterProcessTypesByRuleType(ruleType: RuleType): MutableList<AfterProcessType> {
            return values().filter { it.ruleTypes.contains(ruleType) }.toMutableList()
        }
    }
}

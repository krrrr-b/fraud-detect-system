package com.skeleton.webflux.common.constant.verify

enum class VerifiedResultType(
    val pattern: String,
    val message: String
) {
    OK("정상", "정상 거래입니다."),
    DETECTION("이상금융거래탐지", "이상금융거래로 판단되는 행동이 확인되어 서비스 이용이 제한되었습니다."),
    ERROR("검증 실패", "거래를 검증하는 도중에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
}

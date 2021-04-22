package com.skeleton.webflux.common.constant.transaction

enum class TransactionFlow(val description: String) {
    // @description 실시간 검증
    withdraw("출금"),

    // @description 사후 검증
    ex_post_withdraw("출금");

    fun isExAnte(): Boolean {
        return listOf(withdraw).contains(this)
    }

    fun isExPost(): Boolean {
        return listOf(ex_post_withdraw).contains(this)
    }
}

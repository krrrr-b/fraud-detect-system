package com.skeleton.webflux.common.constant.verify

enum class VerifyResult {
    PASS, DETECT;

    fun isPassed(): Boolean {
        return PASS == this
    }

    fun isDetect(): Boolean {
        return DETECT == this
    }
}

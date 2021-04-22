package com.skeleton.webflux.common.constant.verify

enum class RuleStep {
    ROOT, BRANCH, LEAF;

    fun isLeafRule(): Boolean {
        return LEAF == this
    }

    fun isRootOrBranchRule(): Boolean {
        return listOf(ROOT, BRANCH).contains(this)
    }
}

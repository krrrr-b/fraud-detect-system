package com.skeleton.webflux.internal.api.endpoint.detect.service.component.assigner.interfaces

import com.skeleton.webflux.common.constant.verify.RuleType
import reactor.core.publisher.Mono

interface Releaser {
    fun release(userId: Long, ruleType: RuleType, code: String): Mono<Boolean>
}

package com.skeleton.webflux.internal.api.endpoint.verify.service.component.publisher

import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.verifier.ExPostVerifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ExPostEventPublisher(
) {
    fun publish(detectRules: MutableList<RuleType>, verifier: ExPostVerifier): Mono<Void> = Mono.empty()
}

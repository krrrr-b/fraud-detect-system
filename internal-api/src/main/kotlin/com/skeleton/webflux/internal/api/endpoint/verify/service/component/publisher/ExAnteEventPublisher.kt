package com.skeleton.webflux.internal.api.endpoint.verify.service.component.publisher

import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.verifier.ExAnteVerifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ExAnteEventPublisher {
    fun publish(ruleType: RuleType, verifier: ExAnteVerifier): Mono<Void> = Mono.empty()
}

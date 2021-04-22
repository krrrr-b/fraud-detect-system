package com.skeleton.webflux.internal.api.endpoint.detect.service.component.publisher.items

import com.skeleton.webflux.data.mongo.detect.FraudDetection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class FraudDetectSyncEventPublisher(
) {
    fun process(fraudDetection: FraudDetection): Mono<Void> {
        return Mono.empty()
    }

    private fun sendEvent(payload: String): Mono<Void> {
        return Mono.just(payload)
            .then()
    }
}

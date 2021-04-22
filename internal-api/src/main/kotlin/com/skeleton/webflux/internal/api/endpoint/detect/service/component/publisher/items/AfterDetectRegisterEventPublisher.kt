package com.skeleton.webflux.internal.api.endpoint.detect.service.component.publisher.items

import com.skeleton.webflux.data.mongo.detect.FraudDetection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AfterDetectRegisterEventPublisher<T> where T : FraudDetection {
    fun process(obj: Mono<T>): Mono<T> {
        return obj.doOnNext {
        }
    }
}

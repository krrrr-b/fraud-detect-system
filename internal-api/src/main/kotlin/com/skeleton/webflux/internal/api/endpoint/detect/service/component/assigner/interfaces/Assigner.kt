package com.skeleton.webflux.internal.api.endpoint.detect.service.component.assigner.interfaces

import reactor.core.publisher.Mono

interface Assigner<R, S> {
    fun assign(userId: Long, command: R): Mono<S>
}

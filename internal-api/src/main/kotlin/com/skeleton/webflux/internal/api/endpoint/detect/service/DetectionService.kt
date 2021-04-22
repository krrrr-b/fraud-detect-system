package com.skeleton.webflux.internal.api.endpoint.detect.service

import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ExAnteDetectionCommand
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ExPostDetectionCommand
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ReleaseDetectionCommand
import reactor.core.publisher.Mono

interface DetectionService {
    fun detect(userId: Long, command: ExAnteDetectionCommand): Mono<Boolean>
    fun detect(userId: Long, command: ExPostDetectionCommand): Mono<Boolean>
    fun releaseDetected(userId: Long, command: ReleaseDetectionCommand): Mono<Boolean>
    fun findUserDetectBlockStatus(userId: Long): Mono<VerifiedResultResource>
}

package com.skeleton.webflux.internal.api.endpoint.detect.spec

import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ExAnteDetectionCommand
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ExPostDetectionCommand
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ReleaseDetectionCommand
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import reactor.core.publisher.Mono

@Tag(name = "이상금융거래탐지 데이터 공유")
interface DetectionControllerSpec {
    @Operation(summary = "이상금융거래 사전 탐지 등록")
    fun exAnteDetect(
        userId: Long,
        command: ExAnteDetectionCommand
    ): Mono<Boolean>

    @Operation(summary = "이상금융거래 사후 탐지 등록")
    fun exPostDetect(
        userId: Long,
        command: ExPostDetectionCommand
    ): Mono<Boolean>

    @Operation(summary = "이상금융거래 차단 해제")
    fun releaseDetected(
        userId: Long,
        command: ReleaseDetectionCommand
    ): Mono<Boolean>

    @Operation(summary = "이상금융거래 차단 상태 조회")
    fun findDetectBlockStatus(
        userId: Long
    ): Mono<VerifiedResultResource>
}

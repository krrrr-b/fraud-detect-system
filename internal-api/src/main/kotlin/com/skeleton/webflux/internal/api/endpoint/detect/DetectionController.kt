package com.skeleton.webflux.internal.api.endpoint.detect

import com.skeleton.webflux.internal.api.endpoint.detect.service.DetectionService
import com.skeleton.webflux.internal.api.endpoint.detect.spec.DetectionControllerSpec
import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ExAnteDetectionCommand
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ExPostDetectionCommand
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ReleaseDetectionCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

const val REQUEST_PATH_DETECT = "/api/detect"

const val REQUEST_PATH_EX_POST_DETECT = "/ex-post"
const val REQUEST_PATH_EX_ANTE_DETECT = "/ex-ante"
const val REQUEST_PATH_USER_ID = "/user/{userId}"

@RestController
@RequestMapping(value = [REQUEST_PATH_DETECT])
class DetectionController @Autowired constructor(
    private val detectionService: DetectionService,
) : DetectionControllerSpec {
    @PostMapping(REQUEST_PATH_EX_ANTE_DETECT + REQUEST_PATH_USER_ID)
    override fun exAnteDetect(
        @PathVariable userId: Long,
        @Valid @RequestBody command: ExAnteDetectionCommand
    ): Mono<Boolean> {
        return detectionService.detect(userId, command)
    }

    @PostMapping(REQUEST_PATH_EX_POST_DETECT + REQUEST_PATH_USER_ID)
    override fun exPostDetect(
        @PathVariable userId: Long,
        @Valid @RequestBody command: ExPostDetectionCommand
    ): Mono<Boolean> {
        return detectionService.detect(userId, command)
    }

    @PutMapping(REQUEST_PATH_USER_ID)
    override fun releaseDetected(
        @PathVariable userId: Long,
        @Valid @RequestBody command: ReleaseDetectionCommand
    ): Mono<Boolean> {
        return detectionService.releaseDetected(userId, command)
    }

    @GetMapping(REQUEST_PATH_USER_ID)
    override fun findDetectBlockStatus(
        @PathVariable userId: Long
    ): Mono<VerifiedResultResource> {
        return detectionService.findUserDetectBlockStatus(userId)
    }
}

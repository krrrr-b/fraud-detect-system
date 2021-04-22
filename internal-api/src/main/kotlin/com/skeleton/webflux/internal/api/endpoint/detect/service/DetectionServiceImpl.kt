package com.skeleton.webflux.internal.api.endpoint.detect.service

import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.internal.api.endpoint.detect.service.component.assigner.BlackListAssigner
import com.skeleton.webflux.internal.api.endpoint.detect.service.component.assigner.ExPostDetectAssigner
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ExAnteDetectionCommand
import com.skeleton.webflux.internal.api.endpoint.detect.service.component.DetectionFinder
import com.skeleton.webflux.internal.api.endpoint.detect.service.component.assigner.ExAnteDetectAssigner
import com.skeleton.webflux.internal.api.endpoint.detect.service.component.validator.DetectValidator
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ExPostDetectionCommand
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ReleaseDetectionCommand
import com.skeleton.webflux.internal.api.endpoint.detect.spec.resource.VerifiedResultResource
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class DetectionServiceImpl(
    private val exAnteDetectAssigner: ExAnteDetectAssigner,
    private val exPostDetectAssigner: ExPostDetectAssigner,
    private val blackListAssigner: BlackListAssigner,
    private val detectionFinder: DetectionFinder,
    private val detectValidator: DetectValidator,
) : DetectionService {
    override fun detect(
        userId: Long,
        command: ExAnteDetectionCommand,
    ): Mono<Boolean> {
        return detectValidator.valid(command, userId)
            .flatMap {
                when (command.getRuleType()) {
                    RuleType.ACCOUNT_C3 -> blackListAssigner.assign(userId, command)
                    else -> exAnteDetectAssigner.assign(userId, command)
                }
            }
            .thenReturn(true)
    }

    override fun detect(
        userId: Long,
        command: ExPostDetectionCommand,
    ): Mono<Boolean> {
        return detectValidator.valid(command, userId)
            .flatMap { exPostDetectAssigner.assign(userId, command) }
            .thenReturn(true)
    }

    override fun releaseDetected(userId: Long, command: ReleaseDetectionCommand): Mono<Boolean> {
        return when (command.getRuleType()) {
            RuleType.ACCOUNT_C3 -> blackListAssigner.release(command)
            else -> exPostDetectAssigner.release(userId, command.getRuleType(), command.code)
        }.thenReturn(true)
    }

    override fun findUserDetectBlockStatus(userId: Long): Mono<VerifiedResultResource> {
        return detectionFinder.findDetectBlockStatusByUserId(userId)
            .onErrorReturn(VerifiedResultResource.ok())
    }
}

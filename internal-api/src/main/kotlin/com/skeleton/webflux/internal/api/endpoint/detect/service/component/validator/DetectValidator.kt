package com.skeleton.webflux.internal.api.endpoint.detect.service.component.validator

import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.exception.IllegalUserArgumentException
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ExAnteDetectionCommand
import com.skeleton.webflux.internal.api.endpoint.detect.spec.command.ExPostDetectionCommand
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DetectValidator {
    fun valid(command: ExAnteDetectionCommand, gaNo: Long): Mono<Boolean> {
        if (RuleType.PASS == command.getRuleType()) {
            return Mono.error(IllegalUserArgumentException("[${RuleType.PASS}] 룰은 등록할 수 없습니다."))
        }

        if (listOf(RuleType.ACCOUNT_C4).contains(command.getRuleType())) {
            return Mono.error(IllegalUserArgumentException("사전 탐지는 [${RuleType.ACCOUNT_C4}] 룰을 등록할 수 없습니다."))
        }

        return Mono.just(true)
    }

    fun valid(command: ExPostDetectionCommand, gaNo: Long): Mono<Boolean> {
        if (command.getRuleType() == RuleType.PASS) {
            return Mono.error(IllegalUserArgumentException("[${RuleType.PASS}] 룰은 등록할 수 없습니다."))
        }

        if (listOf(RuleType.ACCOUNT_C3).contains(command.getRuleType())) {
            return Mono.error(IllegalUserArgumentException("사후 탐지는 [${RuleType.ACCOUNT_C3}] 룰을 등록할 수 없습니다."))
        }

        return Mono.just(true)
    }
}

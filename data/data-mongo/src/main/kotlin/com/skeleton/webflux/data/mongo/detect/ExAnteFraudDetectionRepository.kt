package com.skeleton.webflux.data.mongo.detect

import com.skeleton.webflux.data.mongo.detect.ExAnteFraudDetection
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ExAnteFraudDetectionRepository : ReactiveMongoRepository<ExAnteFraudDetection, String> {
    fun findAllByUserIdAndActive(
        userId: Long,
        active: Boolean,
    ): Flux<ExAnteFraudDetection>
}

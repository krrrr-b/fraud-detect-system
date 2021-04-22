package com.skeleton.webflux.internal.api.endpoint.verify.service.event

import com.skeleton.webflux.data.mongo.detect.FraudDetection
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.cache.LastVerifiedRuleTypeCache
import org.bson.Document
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.mapping.event.ReactiveAfterSaveCallback
import org.springframework.data.mongodb.core.mapping.event.ReactiveBeforeSaveCallback
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DocumentCallbackEvents<T>(
    private val lastVerifiedRuleTypeCache: LastVerifiedRuleTypeCache,
) : ReactiveBeforeSaveCallback<T>,
    ReactiveAfterSaveCallback<T> {

    override fun onBeforeSave(entity: T, document: Document, collection: String): Publisher<T> {
        return Mono.just(entity)
    }

    override fun onAfterSave(entity: T, document: Document, collection: String): Publisher<T> {
        if (entity is FraudDetection) {
            // @description [fraud_detection] collection 의 [user_id] 탐지 데이터가 변경이 되면 레디스 데이터 초기화
            return lastVerifiedRuleTypeCache.release(entity.userId)
                .thenReturn(entity)
        }

        return Mono.just(entity)
    }
}

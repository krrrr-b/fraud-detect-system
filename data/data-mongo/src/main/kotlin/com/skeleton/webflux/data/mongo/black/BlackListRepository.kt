package com.skeleton.webflux.data.mongo.black

import com.skeleton.webflux.common.constant.verify.TargetKey
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface BlackListRepository : ReactiveMongoRepository<BlackList, String> {
    fun findByTargetAndActive(target: Map<TargetKey, String>, active: Boolean): Mono<BlackList>
    fun findByTargetInAndActive(target: Map<TargetKey, String>, active: Boolean): Mono<BlackList>
}

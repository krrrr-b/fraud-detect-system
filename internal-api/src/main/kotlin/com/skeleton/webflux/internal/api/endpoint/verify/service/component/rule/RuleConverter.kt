package com.skeleton.webflux.internal.api.endpoint.verify.service.component.rule

import com.skeleton.webflux.common.constant.common.ErrorType
import com.skeleton.webflux.common.constant.verify.RuleType
import com.skeleton.webflux.common.exception.ApiException
import com.skeleton.webflux.internal.api.endpoint.verify.service.component.interfaces.Rule
import com.skeleton.webflux.internal.api.endpoint.verify.spec.query.TransactionQuery
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

@Component
class RuleConverter(
    private val applicationContext: ApplicationContext
) {
    @Suppress("UNCHECKED_CAST")
    fun convert(ruleType: RuleType): Rule<TransactionQuery> {
        return applicationContext.getBeansOfType(Rule::class.java).values
            .stream()
            .filter { it.getRuleType() == ruleType }
            .findFirst()
            .orElseThrow { throw IllegalArgumentException("[${ruleType}] 를 읽을 수 없습니다.") } as? Rule<TransactionQuery>
            ?: throw ApiException(ErrorType.API_INTERNAL_ERROR)
    }
}

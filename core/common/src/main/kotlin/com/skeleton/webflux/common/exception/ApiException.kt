package com.skeleton.webflux.common.exception

import com.skeleton.webflux.common.constant.common.ErrorType
import lombok.Getter

@Getter
open class ApiException(val errorType: ErrorType) : RuntimeException(errorType.message)

package com.skeleton.webflux.common.exception

import lombok.Getter

@Getter
open class NotFoundException(override val message: String?) : RuntimeException(message)

package com.skeleton.webflux.common.exception

import lombok.Getter

@Getter
open class IllegalUserArgumentException(override val message: String?) : RuntimeException(message)

package dev.zwander.common.exceptions

class UnauthorizedException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

class ForbiddenException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

class MissingTokenException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

class RedirectException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

class BadRequestException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

class GatewayTimeoutException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

class OtherStatusException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

fun pickExceptionForStatus(code: Int, message: String, cause: Throwable? = null): Exception {
    return when (code) {
        401 -> UnauthorizedException(message, cause)
        403 -> ForbiddenException(message, cause)
        200 -> MissingTokenException(message, cause)
        301, 302 -> RedirectException(message, cause)
        400 -> BadRequestException(message, cause)
        504 -> GatewayTimeoutException(message, cause)
        else -> OtherStatusException(message, cause)
    }
}

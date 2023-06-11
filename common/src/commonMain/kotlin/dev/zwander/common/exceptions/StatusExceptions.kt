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

class OtherStatusException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

fun pickExceptionForStatus(code: Int, message: String, cause: Throwable? = null): Exception {
    return when (code) {
        401 -> UnauthorizedException(message, cause)
        403 -> ForbiddenException(message, cause)
        else -> OtherStatusException(message, cause)
    }
}

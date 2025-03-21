package com.orca.club.exception

import com.orca.club.exception.BaseException

class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: String,

    ) {
    val serviceName = "club"

    constructor(ex: BaseException) : this(
        code = ex.code,
        message = ex.message,
        timestamp = ex.timeStamp,
    )
}
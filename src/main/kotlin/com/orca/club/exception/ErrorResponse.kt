package com.orca.club.exception

import com.orca.club.utils.getCurrentTimestamp

class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: String,

    ) {
    var serviceName = "club"

    constructor(ex: BaseException) : this(
        code = ex.code,
        message = ex.message,
        timestamp = ex.timeStamp,
    )

    constructor(ex: ExternalException) : this(
        code = "EXTERNAL_SERVER_EXCEPTION",
        message = ex.message,
        timestamp = getCurrentTimestamp()
    )
}
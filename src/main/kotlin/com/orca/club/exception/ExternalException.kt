package com.orca.club.exception

import org.springframework.http.HttpStatus

class ExternalException(
    val httpStatus: HttpStatus,
    serviceName: String,
): RuntimeException() {
    override val message = "Exception occurred from $serviceName server."
}
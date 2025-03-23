package com.orca.club.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus? = HttpStatus.NOT_FOUND, val message: String) {
    UNDEFINED_EXCEPTION(status = HttpStatus.INTERNAL_SERVER_ERROR, message = "Sorry, undefined exception"),

    CLUB_NOT_FOUND(message = "Club not found."),
    JOIN_APPLICATION_NOT_FOUND(message = "Join Application not found."),
    ALREADY_ACCEPTED(status = HttpStatus.BAD_REQUEST, message = "Join request already been accepted."),
    JOIN_APPLICATION_DUPLICATED(status = HttpStatus.BAD_REQUEST, message = "Already have pending join request"),

    DUPLICATE_NAME(status = HttpStatus.BAD_REQUEST, message = "Club name is duplicated.")

}
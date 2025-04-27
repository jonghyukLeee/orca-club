package com.orca.club.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus? = HttpStatus.NOT_FOUND, val message: String) {
    UNDEFINED_EXCEPTION(status = HttpStatus.INTERNAL_SERVER_ERROR, message = "Sorry, undefined exception"),
    BAD_REQUEST(status = HttpStatus.BAD_REQUEST, message = "Bad request. check API documents."),

    CLUB_NOT_FOUND(message = "Club not found."),
    JOIN_APPLICATION_NOT_FOUND(message = "Join Application not found."),
    ALREADY_ACCEPTED(status = HttpStatus.BAD_REQUEST, message = "Join request already been accepted."),
    JOIN_APPLICATION_DUPLICATED(status = HttpStatus.BAD_REQUEST, message = "Already have pending join request"),
    JOIN_ACCEPT_FAILED(status = HttpStatus.BAD_REQUEST, message = "Join process failed"),
    ALREADY_JOINED(status = HttpStatus.BAD_REQUEST, message = "Already joined player."),
    PLAYER_NOT_FOUND(status = HttpStatus.BAD_REQUEST, message = "Player not found."),
    BLACKLISTED_PLAYER(status = HttpStatus.CONFLICT, message = "Is blacklisted player."),
    APPLICATION_CLOSED(status = HttpStatus.CONFLICT, message = "Club is currently closed."),
    DUPLICATE_NAME(status = HttpStatus.BAD_REQUEST, message = "Club name is duplicated."),

    // external
    REDIS_KEY_NOT_FOUND(message = "Key does not exist in Redis"),
    JSON_KEY_NOT_FOUND(message = "Invalid json key"),
}
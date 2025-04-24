package com.orca.club.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun getCurrentTimestamp(): String {
    return LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

fun <T> baseResponse(status: HttpStatus = HttpStatus.OK, body: T): ResponseEntity<T> {
    return ResponseEntity.status(status).body(body)
}

fun buildQueryById(id: ObjectId): Query {
    return Query(Criteria.where("_id").`is`(id))
}

fun generateTransactionId(): String {
    return "txId:${UUID.randomUUID()}"
}

fun Any.toJsonString(): String {
    return ObjectMapper().writeValueAsString(this)
}

fun String.getJsonValue(key: String): String? {
    val mapper = jacksonObjectMapper()
    val node: JsonNode = mapper.readTree(this)
    return node[key]?.asText()
}
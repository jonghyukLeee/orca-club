package com.orca.club.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Document(collection = "join_applications")
data class JoinApplication(
    @Id
    val id: ObjectId? = null,
    val clubId: ObjectId,
    val playerId: ObjectId,
    val status: JoinApplicationStatus = JoinApplicationStatus.PENDING,
    val createdAt: Instant = Instant.now()
) {
    fun getCreateAtAsKST(): String {
        val formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("Asia/Seoul"))
        return formatter.format(this.createdAt)
    }
}
package com.orca.club.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "reviews")
data class Review(
    @Id
    val id: ObjectId? = null,
    val point: Double,
    val comment: String
)
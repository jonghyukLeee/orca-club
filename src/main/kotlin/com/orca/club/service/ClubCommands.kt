package com.orca.club.service

import org.bson.types.ObjectId

data class CreateClubCommand(
    val playerId: ObjectId,
    val name: String,
    val introduction: String = ""
)
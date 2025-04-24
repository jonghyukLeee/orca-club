package com.orca.club.external.player

import org.bson.types.ObjectId

data class PlayerResponse(
    val playerId: ObjectId,
    val name: String,
    val birth: String
)
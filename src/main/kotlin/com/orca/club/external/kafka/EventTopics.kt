package com.orca.club.external.kafka

object EventTopics {
    // Player
    const val PLAYER_UPDATED = "player-updated"

    // Club
    const val CLUB_CREATED = "club-created"
    const val CLUB_CREATE_FAILED = "club-create-failed"
    const val JOIN_ACCEPTED = "join-accepted"
    const val JOIN_ACCEPT_FAILED = "join-accept-failed"
}
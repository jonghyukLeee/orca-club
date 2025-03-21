package com.orca.club.api

data class GenerateRequest(
    val name: String,
    val introduction: String = ""
)

data class UpdateRequest(
    val id: String,
    val name: String,
    val introduction: String
)
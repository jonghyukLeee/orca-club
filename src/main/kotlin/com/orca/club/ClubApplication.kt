package com.orca.club

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ClubApplication

suspend fun main(args: Array<String>) {
	runApplication<ClubApplication>(*args)
}

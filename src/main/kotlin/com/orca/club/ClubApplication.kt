package com.orca.club

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class ClubApplication

suspend fun main(args: Array<String>) {
	runApplication<ClubApplication>(*args)
}

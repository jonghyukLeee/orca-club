package com.orca.club.domain

enum class JoinApplicationStatus(val notification: String) {
    PENDING("응답 대기"),
    ACCEPTED("수락됨"),
    REJECTED("거절됨")
}
package com.orca.club.api

import com.orca.club.service.CreateClubCommand
import io.swagger.v3.oas.annotations.media.Schema
import org.bson.types.ObjectId

@Schema(description = "클럽 생성 RequestDTO")
data class CreateClubRequest(
    @field:Schema(description = "Player ID (구단주)")
    val playerId: String,
    @field:Schema(description = "클럽 이름")
    val name: String,
    @field:Schema(description = "클럽 소개")
    val introduction: String = ""
) {
    fun toCommand(): CreateClubCommand {
        return CreateClubCommand(
            playerId = ObjectId(this.playerId),
            name = name,
            introduction = introduction,
        )
    }
}

@Schema(description = "클럽 수정 RequestDTO")
data class UpdateRequest(
    @field:Schema(description = "Club ID")
    val id: String,
    @field:Schema(description = "클럽 이름")
    val name: String?,
    @field:Schema(description = "클럽 소개")
    val introduction: String?
)

@Schema(description = "Club ResponseDTO")
data class ClubResponse(
    @field:Schema(description = "Club ID")
    val id: String,
    @field:Schema(description = "클럽 이름")
    val name: String,
    @field:Schema(description = "클럽 소개")
    val introduction: String,
    @field:Schema(description = "승")
    val win: Int,
    @field:Schema(description = "패")
    val lose: Int,
    @field:Schema(description = "선수단")
    val players: List<PlayerResponse>,
    @field:Schema(description = "클럽 평가 목록")
    val reviews: List<ReviewResponse>,
    @field:Schema(description = "매너 점수")
    val mannerPoint: Double,
    @field:Schema(description = "블랙 리스트")
    val blacklist: List<String>,
    @field:Schema(description = "클럽 모집 상태 (OPEN / CLOSE)")
    val status: String,
)

@Schema(description = "Player ResponseDTO")
data class PlayerResponse(
    @field:Schema(description = "Player ID")
    val id: String,
    @field:Schema(description = "선수 이름")
    val name: String,
    @field:Schema(description = "직책")
    val role: String,
    @field:Schema(description = "포지션 (FW / MF / DF)")
    val position: String? = null,
    @field:Schema(description = "경기 참여 횟수")
    val matchCount: Int,
    @field:Schema(description = "득점")
    val goal: Int,
    @field:Schema(description = "도움")
    val assist: Int,
    @field:Schema(description = "MOM")
    val momCount: Int,
    @field:Schema(description = "선수 상태 (ACTIVE / INACTIVE / WITHDRAWN)")
    val status: String
)

@Schema(description = "클럽 평가 ResponseDTO")
data class ReviewResponse(
    @field:Schema(description = "클럽 평가 ID")
    val id: String,
    @field:Schema(description = "평가 점수")
    val point: Double,
    @field:Schema(description = "평가 내용")
    val comment: String,
)

@Schema(description = "클럽 참가 신청 RequestDTO")
data class JoinRequest(
    @field:Schema(description = "Club ID")
    val clubId: String,
    @field:Schema(description = "Player ID")
    val playerId: String
)

@Schema(description = "클럽 참가 신청 ResponseDTO")
data class JoinApplicationResponse(
    @field:Schema(description = "JoinApplication ID")
    val joinApplicationId: String,
    @field:Schema(description = "Club ID")
    val clubId: String,
    @field:Schema(description = "Player ID")
    val playerId: String,
    @field:Schema(description = "신청 상태 (PENDING / ACCEPTED / REJECTED)")
    val status: String,
    @field:Schema(description = "신청 상태 메시지")
    val notification: String,
    @field:Schema(description = "신청 시간")
    val createdAt: String
)
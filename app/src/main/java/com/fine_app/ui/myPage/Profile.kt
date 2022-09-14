package com.fine_app.ui.myPage

import java.io.Serializable
import java.time.LocalDateTime

// DTO 클래스 생성
data class Profile (
    val id: Long, // 회원 ID
    val userImageNum: Int, // 프로필 사진
    val nickname: String, //닉네임
    val email: String?, // 이메일 주소
    var intro: String?,
    val userIntroduction: String?, // 사용자 소개글
    val userUniversity: String?, // 대학명
    val userCollege: String?, // 단과대명
    val userPhoneNumber: String?, // 전화번호
    val userResidence: String?, // 거주지
    val level: String, // 사용자 레벨
    val report: Int?, // 신고 횟수
    val keyword1: String,
    val keyword2: String,
    val keyword3: String,
    var follower: Int,
    val roomCollectionList: ArrayList<Friend>,
)

data class Friend(
    val friendId: Long,
    val nickname: String,
    val intro: String,
    val level : String,
): Serializable

data class DataSearch(
    val dataSearch: Content,
)

data class Content(
    val content: ArrayList<University>,
)

data class University(
    val campusName: String,
    val schoolName: String,
    val link: String,
)

data class UniversityDto(
    val token: String,
    val university: String,
    val updateDate: LocalDateTime
)
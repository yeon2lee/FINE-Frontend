package com.fine_app.ui.MyPage

// DTO 클래스 생성
data class Profile (
    val id: Long, // 회원 ID
    val nickname: String, //닉네임
    val email: String?, // 이메일 주소
    var intro: String?,
    val userIntroduction: String?, // 사용자 소개글
    val userUniversity: String?, // 대학명
    val userCollege: String?, // 단과대명
    val userPhoneNumber: String?, // 전화번호
    val userResidence: String?, // 거주지
    val level: String, // 사용자 레벨
    val report: Int? // 신고 횟수
)


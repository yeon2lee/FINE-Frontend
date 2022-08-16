package com.fine_app.ui.myPage

import com.fine_app.ui.myPage.RequestAuthData
import com.fine_app.ui.myPage.RequestProfileData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// 서비스 인터페이스 생성
interface IMypageService {
    // 회원가입
    @POST("signup")
    fun signUp(
        @Body user: RequestAuthData
    ): Call<Profile>

    // 회원 탈퇴
    @GET("mypage/{memberId}")
    fun userSecession(@Path("memberId") memberId: Long): Call<Profile>

    // 전화번호로 인증 번호 전송
    @POST("authMessage/{memberId}")
    fun sendAuthMesssage(
        @Path("memberId") memberId: Long,
        @Body phoneNumber: String
    ): Call<Long>

    // 번호 인증 최종
    @POST("phoneVerification/{memberId}")
    fun verifyAuth(
        @Path("memberId") memberId: Long,
        @Body token: String
    ): Call<Long>

    // 프로필 생성
    @GET("mypage/{memberId}")
    fun getMyProfile(@Path("memberId") memberId: Long): Call<Profile>

    // 내가 쓴 글 목록
    @GET("mypage/myPost/{memberId}")
    fun getMyPostList(@Path("memberId") memberId: Long): Call<List<Post>>

    // 북마크 목록
    @GET("mypage/bookmark/{memberId}")
    fun getMyBookmarkList(@Path("memberId") memberId: Long): Call<List<Post>>

    // 그룹 신청글 목록
    @GET("mypage/myGroupPost/{memberId}")
    fun getMyGroupList(@Path("memberId") memberId: Long): Call<List<Post>>

    // 프로필 수정
    @POST("mypage/editProfile/{memberId}")
    fun editProfile(
        @Path("memberId") memberId: Long,
        @Body user: RequestProfileData
    ): Call<Profile>

    // 프로필 조회
    @GET("profile/{memberId}")
    fun getFriendProfile(@Path("memberId") memberId: Long): Call<Profile>
}
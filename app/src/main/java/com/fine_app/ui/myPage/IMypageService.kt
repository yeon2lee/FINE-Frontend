package com.fine_app.ui.MyPage

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// 서비스 인터페이스 생성
interface IMypageService {
    // 회원가입
    @POST("signUp")
    fun signUp(
        @Body user:RequestAuthData
    ): Call<Profile>

    // 회원 탈퇴
    @GET("mypage/{memberId}")
    fun userSecession(@Path("memberId") memberId: Long): Call<Profile>

    // 프로필 생성
    @GET("myPage/{memberId}")
    fun getMyProfile(@Path("memberId") memberId: Long): Call<Profile>

    // 내가 쓴 글 목록
    @GET("myPage/myPost/{memberId}")
    fun getMyPostList(@Path("memberId") memberId: Long): Call<List<Post>>

    // 북마크 목록
    @GET("myPage/bookmark/{memberId}")
    fun getMyBookmarkList(@Path("memberId") memberId: Long): Call<List<Post>>

    // 프로필 수정
    @POST("myPage/editProfile/{memberId}")
    fun editProfile(
        @Path("memberId") memberId: Long,
        @Body user:RequestProfileData
    ): Call<Profile>

    // 프로필 조회
    @GET("profile/{memberId}")
    fun getFriendProfile(@Path("memberId") memberId: Long): Call<Profile>
}
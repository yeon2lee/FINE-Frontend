package com.fine_app.retrofit


import com.fine_app.Post
import com.fine_app.Posting
import com.fine_app.ui.MyPage.Profile
import retrofit2.Call
import retrofit2.http.*

interface IRetrofit {
    //일반커뮤니티
    @POST("/post/{memberID}") //글 작성
    fun addMainpost(@Path("memberID")  memberID:String, @Body postInfo:Posting):Call<Post>

    @GET("/post/general") //글 목록 조회
    fun viewMainCommunity() :Call<List<Post>>

    @GET("/post/{postingId}") //글 세부 조회
    fun viewMainPosting(@Path("postingId") PostingID:String):Call<Post>

    //그룹커뮤니티
    @GET("/post/group") //글 전체 목록 조회
    fun viewGroupCommunity() : Call<List<Post>>

    @GET("/post/group/proceed") //글 진행 목록 조회
    fun viewGroupCommunityProceed() : Call<List<Post>>

    @GET("/post/group/close") //글 완료 목록 조회
    fun viewGroupCommunityClose() : Call<List<Post>>

    @GET("/post/{postingId}") //글 세부 조회
    fun viewGroupPosting(@Path("postingId") PostingID:String):Call<Post>

    @GET("profile/{memberId}") // 유저 프로필 조회
    fun getUserProfile(@Path("memberId") memberId: Long): Call<Profile>
}
package com.fine_app.retrofit


import com.fine_app.Post
import com.fine_app.Posting
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.POST

interface IRetrofit {
    //일반커뮤니티
    @POST("/post/{memberId}") //글 작성
    fun addMainpost(@Query("memberID")  memberID:String, @Body postInfo:Posting):Call<Post>

    @GET("/post/general") //글 목록 조회
    fun viewMainCommunity() :Call<List<Post>>

    @GET("/post/{postingId}") //글 세부 조회
    fun viewMainPosting(@Query("postingId") PostingID:String):Call<Post>

    //그룹커뮤니티
    @GET("/post/group") //글 전체 목록 조회
    fun viewGroupCommunity() : Call<List<Post>>

    @GET("/post/group/proceed") //글 진행 목록 조회
    fun viewGroupCommunityProceed() : Call<List<Post>>

    @GET("/post/group/close") //글 완료 목록 조회
    fun viewGroupCommunityClose() : Call<List<Post>>

    @GET("/post/{postingId}") //글 세부 조회
    fun viewGroupPosting(@Query("postingId") PostingID:String):Call<Post>
}
package com.fine_app.retrofit

import com.fine_app.*
import retrofit2.Call
import retrofit2.http.*

interface IRetrofit {
    //커뮤니티 공통
    @POST("/post/{memberId}") //글 작성
    fun addPost(@Path("memberId")  memberId:Long, @Body postInfo:Posting):Call<Post>

    @GET("/post/{postingId}") //글 세부 조회
    fun viewPosting(@Path("postingId") postingId:Long):Call<Post>

    //일반 커뮤니티
    @GET("/post/general") //글 목록 조회
    fun viewMainCommunity() :Call<List<Post>>

    @DELETE("/post/{postingId}") //글 삭제
    fun deletePosting(@Path("postingId") postingId:Long):Call<Long>

    @PUT("/post/{postingId}/text") //글 내용 수정
    fun editPosting(@Path("postingId") postingId:Long, text:String):Call<Post>


    //그룹커뮤니티
    @GET("/post/group") //글 전체 목록 조회
    fun viewGroupCommunity() : Call<List<Post>>

    @GET("/post/group/proceed") //글 진행 목록 조회
    fun viewGroupCommunityProceed() : Call<List<Post>>

    @GET("/post/group/close") //글 완료 목록 조회
    fun viewGroupCommunityClose() : Call<List<Post>>

    @POST("/post/{postingId}/{memberId}/join") //참여하기
    fun joinGroup(@Path("postingId") postingId:Long, @Path("memberId") memberId:Long, accept_check:Boolean) :Call<Join>

    @DELETE("post/{recruitingId}/delete") //참여하기 취소
    fun cancelJoinGroup(@Path("recruitingId") recruitingId:Long):Call<Long>

    @POST("{postingId}/{recruitingId}/accept") //참가 수락
    fun acceptJoinGroup(@Path("postingId") postingId:Long, @Path("recruitingId") recruitingId:Long, accept_check:Boolean) :Call<Join>

    @DELETE("/{postingId}/{recruitingId}/accept") //참여하기 수락 취소
    fun cancelAcceptJoinGroup(@Path("postingId") postingId:Long, @Path("recruitingId") recruitingId:Long, accept_check:Boolean):Call<Join>

    @PUT("/post/{postingId}/text") //글 마감여부 변경
    fun editClosing(@Path("postingId") postingId:Long):Call<Post>

    //검색
    @GET("post/search") //커뮤니티 글 검색
    fun searchPosting(@Query("title") title:String) :Call<List<Post>>

    //댓글
    @POST("/comment")
    fun addComment(@Body commentInfo: NewComment):Call<NewComment>

    @PUT("/comment/{commentId}")
    fun editComment(@Path("commentId") commentId:Long, @Body commentInfo: NewComment):Call<Comment>

    @DELETE("/comment/{commentId}")
    fun deleteComment(@Path("commentId") commentId:Long):Call<Long>

    //북마크
    @POST("/bookmark")
    fun addBookMark(@Body BookMark: BookMark):Call<BookMark>

    @DELETE("/bookmark/{bookmarkId}")
    fun deleteBookMark(@Path("bookMarkId") bookMarkId:Long):Call<Long>

    //친구
    @GET("/followlist/{memberId}")
    fun viewFriendList(@Path("memberId") memberId:Long):Call<List<Friend>>

}
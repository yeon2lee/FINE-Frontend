/*
package com.fine_app.retrofit

import android.util.Log
import com.fine_app.Post
import com.fine_app.PostList
import retrofit2.Call
import retrofit2.Response

class RetrofitManager {
    companion object{
        val instance = RetrofitManager()
    }
    //레트로핏 인터페이스 가져오기
    private val iRetrofit : IRetrofit? =RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)

    //메인 커뮤니티 목록
    fun viewMainCommunity(){
        val call = iRetrofit?.viewMainCommunity() ?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : retrofit2.Callback<PostList>{
            //응답성공
            override fun onResponse(call: Call<PostList>, response: Response<PostList>) {
                Log.d("retrofit", "메인커뮤니티목록 - 응답 성공 / t : ${response.raw()}")
            }
            //응답실패
            override fun onFailure(call: Call<PostList>, t: Throwable) {
                Log.d("retrofit", "메인커뮤니티목록 - 응답 실패 / t: $t")
            }
        })
    }
    //메인 커뮤니티 세부 글
    fun viewMainPosting(postingId:String?, completion:(String)->Unit){
        val term:String=postingId.let{it}?:""
        val call = iRetrofit?.viewMainPosting(PostingID = term).let{it}?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : retrofit2.Callback<Post>{
            //응답성공
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "메인 커뮤니티 세부 글 - 응답 성공 / t : ${response.raw()}")
                Log.d("retrofit", response.body().toString())

                completion(response.body().toString())
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "메인 커뮤니티 세부 글 - 응답 실패 / t: $t")
            }

        })
    }
    //그룹 커뮤니티 목록
    fun viewGroupCommunity(){
        val call = iRetrofit?.viewGroupCommunity().let{it}?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : retrofit2.Callback<PostList>{
            //응답성공
            override fun onResponse(call: Call<PostList>, response: Response<PostList>) {
                Log.d("retrofit", "그룹커뮤니티목록 - 응답 성공 / t : ${response.raw()}")

            }
            //응답실패
            override fun onFailure(call: Call<PostList>, t: Throwable) {
                Log.d("retrofit", "그룹커뮤니티목록 - 응답 실패 / t: $t")
            }

        })
    }
    //그룹 커뮤니티 진행 목록
    fun viewGroupCommunityProceed(){
        val call = iRetrofit?.viewGroupCommunityProceed().let{it}?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : retrofit2.Callback<PostList>{
            //응답성공
            override fun onResponse(call: Call<PostList>, response: Response<PostList>) {
                Log.d("retrofit", "그룹커뮤니티진행목록 - 응답 성공 / t : ${response.raw()}")

            }
            //응답실패
            override fun onFailure(call: Call<PostList>, t: Throwable) {
                Log.d("retrofit", "그룹커뮤니티진행목록 - 응답 실패 / t: $t")
            }

        })
    }
    //그룹 커뮤니티 완료 목록
    fun viewGroupCommunityClose(){
        val call = iRetrofit?.viewGroupCommunityClose().let{it}?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : retrofit2.Callback<PostList>{
            //응답성공
            override fun onResponse(call: Call<PostList>, response: Response<PostList>) {
                Log.d("retrofit", "그룹커뮤니티완료목록 - 응답 성공 / t : ${response.raw()}")
            }
            //응답실패
            override fun onFailure(call: Call<PostList>, t: Throwable) {
                Log.d("retrofit", "그룹커뮤니티완료목록 - 응답 실패 / t: $t")
            }
        })
    }
    //그룹 커뮤니티 세부 글
    fun viewGroupPosting(postingId:String?, completion:(String)->Unit){
        val term:String=postingId.let{it}?:""
        val call = iRetrofit?.viewGroupPosting(PostingID = term).let{it}?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : retrofit2.Callback<Post>{
            //응답성공
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "메인 커뮤니티 세부 글 - 응답 성공 / t : ${response.raw()}")
                completion(response.body().toString())
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "메인 커뮤니티 세부 글 - 응답 실패 / t: $t")
            }

        })
    }
}

 */
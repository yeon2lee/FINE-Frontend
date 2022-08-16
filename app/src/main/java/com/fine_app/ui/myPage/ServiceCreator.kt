package com.fine_app.ui.myPage

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
    private const val BASE_URL = "http://54.209.17.39:8080/"

    // Retrofit 객체 생성
    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // 인터페이스 구현 객체 획득
    var service: IMypageService = retrofit.create(IMypageService::class.java)
}
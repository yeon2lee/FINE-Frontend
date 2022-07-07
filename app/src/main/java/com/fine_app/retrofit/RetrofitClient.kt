package com.fine_app.retrofit

import android.util.Log
import com.fine_app.retrofit.API.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

object RetrofitClient {

    private var retrofitClient: Retrofit?=null

    fun getClient(baseUrl:String):Retrofit?{

        Log.d("retrofitTest", "getClient() called")

        //로그용 코드
        //okhttp 인스턴스 생성
        val client=OkHttpClient.Builder()
        //로깅 인터셉터 설정 로그 찍기 위해
        val loggingInterceptor=HttpLoggingInterceptor(object:HttpLoggingInterceptor.Logger{
            override fun log(message: String) {
                Log.d("retrofit", "retrofitClient - log() called / message:$message ")
                when{
                    message.isJsonObject()-> Log.d("retrofit", JSONObject(message).toString(4))
                    message.isJsonArray()-> Log.d("retrofit", JSONObject(message).toString(4))
                    else->{
                        try{
                            Log.d("retrofit", JSONObject(message).toString(4))
                        }catch (e:Exception){
                            Log.d("retrofit", "message")
                        }
                    }
                }
            }
        })
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        client.addInterceptor(loggingInterceptor) //위에서 설정한 로깅 인터셉터 추가

        //기본파라메터 추가
        val baseParameterInterceptor : Interceptor = (object :Interceptor{
            override fun intercept(chain: Interceptor.Chain): Response {
                Log.d("retrofit", "intercept 호출")
                //오리지날 리퀘스트
                val originalRequest=chain.request()
                //쿼리파라메터 추가
                val addedUrl = originalRequest.url.newBuilder().addQueryParameter("client_id", API.CLIENT_ID).build()
                val finalRequest=originalRequest.newBuilder()
                    .url(addedUrl)
                    .method(originalRequest.method, originalRequest.body)
                    .build()
                return chain.proceed(finalRequest)
            }
        })
        //위에서 설정한 기본파라메터 인터셉터를 okhttp 클라이언트에 추가
        client.addInterceptor(baseParameterInterceptor)

        if(retrofitClient==null) { //비어있으면 생성
            //레트로핏 빌더를 통해 인스턴스 생성
           retrofitClient=Retrofit.Builder()
               .baseUrl(baseUrl)
               .addConverterFactory(GsonConverterFactory.create())
               //위에서 설정한 클라이언트로 레트로핏 클라이언트 설정
               //.client(client.build())
               .build()
        }
        return retrofitClient
    }
    private val retrofit by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api : IRetrofit by lazy {
        retrofit.create(IRetrofit::class.java)
    }
}
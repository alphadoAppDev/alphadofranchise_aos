package kr.co.data

import android.util.Log
import com.google.gson.GsonBuilder
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var retrofitClient : Retrofit? = null

    fun getClient(baseUrl : String) : Retrofit?{
        val client = OkHttpClient.Builder()

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("test", "RetrofitClient - log : $message")
        }

        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        client.addInterceptor(loggingInterceptor)

        client.connectTimeout(60, TimeUnit.SECONDS)
        client.readTimeout(60, TimeUnit.SECONDS)
        client.writeTimeout(60, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)

        val gson = GsonBuilder()
            .setLenient()
            .create()

        if(retrofitClient == null) {
            retrofitClient = Retrofit.Builder()
                .baseUrl("$baseUrl")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(client.build())
                .build()
        }
        return retrofitClient
    }
}
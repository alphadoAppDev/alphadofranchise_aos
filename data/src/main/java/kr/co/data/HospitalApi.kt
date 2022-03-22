package kr.co.data

import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Single
import kr.co.domain.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface HospitalApi {
    @GET("/items/hospitals")
    fun getAllHospital(
        @Query("search_type") search_type : String?,
        @Query("search") search : String?,
        @Query("pos_lat") pos_lat : Float?,
        @Query("pos_long") pos_long : Float?,
        @Query("display_level") display_level : String?,
        @Query("address") address : String?,
        @Query("premium_chk") premium_chk : String?,
        @Query("basic_chk") basic_chk : String?,
        @Query("is_show") is_show : Boolean?,
        @Query("is_sale") is_sale : Boolean?,
        @Query("start_at") start_at : String?,
        @Query("end_at") end_at : String?,
        @Query("sort") sort : String?,
        @Query("limit") limit : Int?,
        @Query("page") page : Int?,
    ) : Single<Response<JsonElement>>


    @GET("/items/hospitals/{hospital_id}")
    fun requestGetHospitalDetailInfo(
        @Path("hospital_id") hospital_id : Int
    ) : Single<Response<JsonElement>>

    @GET("/items/hospitals/{hospital_id}/reviews")
    fun requestGetHospitalReviews(
        @Path("hospital_id") hospital_id : Int,
        @Query("page") page : Int,
        @Query("sort") sort : String
    ) : Single<Response<JsonElement>>

    @Multipart
    @POST("/img/review")
    fun requestUploadImage(@Part imageFile: MultipartBody.Part?): Single<ResponseBody>

    @POST("/items/reviews")
    fun requestAddReview(
        @Body reviewParameter: ReviewParameter
    ) : Single<JsonElement>

    @POST("/reviews/{review_id}/like")
    fun requestAddReviewLike(
        @Path("review_id") review_id : Int
    ) : Single<Response<JsonElement>>
}


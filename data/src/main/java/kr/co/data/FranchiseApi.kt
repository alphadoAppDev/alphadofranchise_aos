package kr.co.data

import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Single
import kr.co.domain.model.FranchiseInputCodeResponse
import kr.co.domain.model.FranchiseParameter
import kr.co.domain.model.ReviewParameter
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface FranchiseApi {
    @POST("/api/login")
    @FormUrlEncoded
    fun requestInputStoreCode(
        @FieldMap params : Map<String,String>
    ) : Single<JsonElement>

//    @POST("/api/login")
//    fun requestInputStoreCode(
//        @Body params : FranchiseParameter
//    ) : Single<JsonElement>


    @POST("/year")
    fun requestRegionInfo(
        @Body params : FranchiseParameter
    ) : Single<JsonElement>

    @POST("/year2")
    fun requestRegionInfo2(
        @Body params : FranchiseParameter
    ) : Single<JsonElement>

    @POST("/month")
    @FormUrlEncoded
    fun requestHeadOfficeInfo(
        @FieldMap params : Map<String,String>
    ) : Single<JsonElement>

    @POST("/main")
    @FormUrlEncoded
    fun requestFranchiseSummaryInfo(
        @FieldMap params : Map<String,String>
    ): Single<JsonElement>
}
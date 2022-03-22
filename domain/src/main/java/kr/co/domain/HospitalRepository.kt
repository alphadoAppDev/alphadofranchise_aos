package kr.co.domain

import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Single
import kr.co.domain.model.HospitalAddReviewResponse
import kr.co.domain.model.HospitalDetail
import kr.co.domain.model.HospitalListResponse
import kr.co.domain.model.HospitalReview
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

interface HospitalRepository {
    fun requestGetAllHospital(
        search_type : String?,
        search : String?,
        pos_lat : Float?,
        pos_long : Float?,
        display_level : String?,
        address : String?,
        premium_chk : String?,
        basic_chk : String?,
        is_show : Boolean?,
        is_sale : Boolean?,
        start_at : String?,
        end_at : String?,
        sort : String?,
        limit : Int?,
        page : Int?
    ) : Single<Response<JsonElement>>

    fun requestGetHospitalDetailInfo(hospital_id : Int) : Single<Response<JsonElement>>
    fun requestGetHospitalReviews(hospital_id : Int, page: Int, sort : String) : Single<Response<JsonElement>>
    fun requestUploadImage(imageFile : File) : Single<ResponseBody>
    fun requestAddReview(
        hospitalId : Int, writer : String, description : String,
        facilityScore : Int, examinationScore : Int, kindnessScore: Int,
        likeCount : Int, createdAt : String, urlList : ArrayList<String>
    ) : Single<JsonElement>

    fun requestAddReviewLike(review_id : Int) : Single<Response<JsonElement>>
//    Single<HospitalAddReviewResponse>
}
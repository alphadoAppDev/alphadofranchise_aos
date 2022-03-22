package kr.co.domain

import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Single
import kr.co.domain.model.HospitalAddReviewResponse
import kr.co.domain.model.HospitalDetail
import kr.co.domain.model.HospitalListResponse
import kr.co.domain.model.HospitalReview
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

class HospitalUseCase(private val hospitalRepository: HospitalRepository){
    fun executeGetAllHospital(
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
    ) : Single<Response<JsonElement>> = hospitalRepository.requestGetAllHospital(
        search_type = search_type,
        search = search,
        pos_lat = pos_lat,
        pos_long = pos_long,
        display_level = display_level,
        address = address,
        premium_chk = premium_chk,
        basic_chk = basic_chk,
        is_show = is_show,
        is_sale = is_sale,
        start_at = start_at,
        end_at = end_at,
        sort = sort,
        limit = limit,
        page = page
    )

    fun executeGetHospitalDetailInfo(hospital_id : Int) : Single<Response<JsonElement>> =
        hospitalRepository.requestGetHospitalDetailInfo(hospital_id = hospital_id)

    fun executeGetHospitalReviews(hospital_id : Int, page : Int, sort : String): Single<Response<JsonElement>> =
        hospitalRepository.requestGetHospitalReviews(hospital_id = hospital_id, page = page, sort = sort)

    fun executeUploadImage(imageFile :File) : Single<ResponseBody> =
        hospitalRepository.requestUploadImage(imageFile = imageFile)

    fun executeAddReview(
        hospitalId : Int, writer : String, description : String,
        facilityScore : Int, examinationScore : Int, kindnessScore: Int,
        likeCount : Int, createdAt : String, urlList : ArrayList<String>
    ) : Single<JsonElement> =
        hospitalRepository.requestAddReview(
            hospitalId = hospitalId, writer = writer, description = description, facilityScore = facilityScore,
            examinationScore = examinationScore, kindnessScore = kindnessScore, likeCount = likeCount, createdAt = createdAt, urlList = urlList
        )
    fun executeAddReviewLike(review_id : Int) : Single<Response<JsonElement>> =
        hospitalRepository.requestAddReviewLike(review_id = review_id)
}
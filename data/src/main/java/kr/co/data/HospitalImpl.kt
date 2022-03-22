package kr.co.data

import android.util.Log
import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Single
import kr.co.domain.HospitalRepository
import kr.co.domain.model.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class HospitalImpl(private val hospitalApi : HospitalApi) : HospitalRepository {
    override fun requestGetAllHospital(
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
    ): Single<Response<JsonElement>> {

        return hospitalApi.getAllHospital(
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
    }

    override fun requestGetHospitalDetailInfo(hospital_id: Int): Single<Response<JsonElement>> {
        return hospitalApi.requestGetHospitalDetailInfo(hospital_id = hospital_id)
    }

    override fun requestGetHospitalReviews(hospital_id: Int, page: Int, sort : String): Single<Response<JsonElement>> {
        return hospitalApi.requestGetHospitalReviews(hospital_id = hospital_id, page = page, sort = sort)
    }

    override fun requestUploadImage(imageFile: File): Single<ResponseBody> {

        var multiPartBody : MultipartBody.Part? = null
        try {
            val requestFile = RequestBody.create(MediaType.parse("image/jpg"), imageFile)
            multiPartBody = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
        }catch (e : Exception) {
            e.printStackTrace()
        }

        return hospitalApi.requestUploadImage(multiPartBody)
    }

    override fun requestAddReview(
        hospitalId: Int,
        writer: String,
        description: String,
        facilityScore: Int,
        examinationScore: Int,
        kindnessScore: Int,
        likeCount : Int,
        createdAt : String,
        urlList: ArrayList<String>
    ): Single<JsonElement> {

        val scoreList = ArrayList<ReviewScore>()
        scoreList.add(ReviewScore(hospital_id = hospitalId, type = "hospital", score = facilityScore))
        scoreList.add(ReviewScore(hospital_id = hospitalId, type = "satisfaction", score = examinationScore))
        scoreList.add(ReviewScore(hospital_id = hospitalId, type = "kindness", score = kindnessScore))

        val imageList = ArrayList<ReviewImages>()
        for(element in urlList) {
            imageList.add(ReviewImages(url = element))
        }

        val reviewParameter = ReviewParameter(
            reviewContent = ReviewContent(hospital_id = hospitalId, writer = writer, description = description, like_count = likeCount, created_at = createdAt),
            reviewScore = scoreList,
            images = imageList
        )
        return hospitalApi.requestAddReview(reviewParameter = reviewParameter)
    }

    override fun requestAddReviewLike(review_id: Int): Single<Response<JsonElement>> {
        return hospitalApi.requestAddReviewLike(review_id = review_id)
    }
}
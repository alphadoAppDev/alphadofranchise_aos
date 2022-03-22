package kr.co.domain.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HospitalReview(
    @SerializedName("data")
    val hospitalReviews : ArrayList<HospitalReviewItem>
)

data class HospitalReviewItem(
    @SerializedName("basic")
    var reviewContent : ReviewContent,
    @SerializedName("image")
    val reviewImages : ArrayList<ReviewImages>,
    @SerializedName("score")
    val reviewScores : ArrayList<ReviewScore>,
) : Serializable

data class ReviewContent(
    val hospital_id : Int,
    val writer : String,
    val description : String,
    val id : Int? = null,
    var like_count : Int,
    val created_at : String,
) : Serializable

data class ReviewImages(
    val url : String,
    val id : Int? = null,
    val review_id : Int? = null,
) : Serializable

data class ReviewScore(
    val hospital_id: Int,
    val type : String,
    val score : Int,
    val id : Int? = null,
    val review_id : Int? = null,
) : Serializable
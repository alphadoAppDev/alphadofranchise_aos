package kr.co.domain.model

class HospitalAddReviewResponse(
    val hospital_id : Int,
    val writer : String,
    val description : String,
    val id : Int,
    val like_count : Int,
)
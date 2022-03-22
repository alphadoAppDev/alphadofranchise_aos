package kr.co.alphadopetshop.model

data class ReviewListModel(
    var examinationScore : Int,
    var facilityScore : Int,
    var kindnessScore : Int,
    var id : String,
    var date : String,
    var content : String,
    var likeCount : Int
)
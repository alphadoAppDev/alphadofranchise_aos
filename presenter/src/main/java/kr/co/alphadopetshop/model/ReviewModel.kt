package kr.co.alphadopetshop.model

data class ReviewModel(
    var examinationSatisfaction : Int? = null,
    var facilitySatisfaction : Int? = null,
    var kindnessSatisfaction : Int? = null,
    var review : String? = null
)
package kr.co.alphadopetshop.model

import kr.co.domain.model.HospitalReviewItem
import java.util.ArrayList

class ReviewUpdateModel(
    val position : Int,
    val data: Any,
    val list : MutableList<HospitalReviewItem>
)
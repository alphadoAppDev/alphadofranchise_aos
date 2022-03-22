package kr.co.domain.model

import com.google.gson.annotations.SerializedName

data class ReviewParameter(
    @SerializedName("basic")
    val reviewContent : ReviewContent,
    @SerializedName("score")
    val reviewScore : ArrayList<ReviewScore>,
    @SerializedName("image")
    val images : ArrayList<ReviewImages>
)


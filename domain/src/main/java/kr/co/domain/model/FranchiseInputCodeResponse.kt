package kr.co.domain.model

import java.io.Serializable

data class FranchiseInputCodeResponse(
    val result : String,
    val message : String,
    val data : Any
) : Serializable

data class StoreCode(
    val code : String,
    val region : String
) : Serializable
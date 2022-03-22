package kr.co.domain.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FranchiseProfitInfo(
    val result : String,
    val message : String,
    val data : ArrayList<ProfitInfo>
) : Serializable

data class ProfitInfo(
    val date : String,
    val profit: String,
    val table: ProfitTable,
    val title : String,
    var unit : String
) : Serializable

data class ProfitTable(
    @SerializedName("line01")
    val row0 : ArrayList<String>,
    @SerializedName("line02")
    val row1 : ArrayList<String>,
    @SerializedName("line03")
    val row2 : ArrayList<String>
) : Serializable
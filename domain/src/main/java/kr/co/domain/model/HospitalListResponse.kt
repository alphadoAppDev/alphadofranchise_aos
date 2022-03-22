package kr.co.domain.model

import java.io.Serializable

data class HospitalListResponse(
    val data : ArrayList<HospitalInfo>
) : Serializable

data class HospitalInfo(
    val name : String? = null,
    val tel : String? = null,
    val mainDoctor : String? = null,
    val introduce1 : String? = null,
    val introduce2 : String? = null,
    val introduce3 : String? = null,
    val address : String? = null,
    val addr_lat : String? = null,
    val addr_long : String? = null,
    val enable_parking  :Boolean? = null,
    val directions : String? = null,
    val holiday_open_time : String? = null,
    val holiday_close_time : String? = null,
    val lunch_start_time : String? = null,
    val lunch_end_time : String? = null,
    val premium_chk : Boolean? = null,
    val premium_price : Int? = null,
    val premium_sale_price : Int? = null,
    val premium_care_time : String? = null,
    val premium_standard_unit : String? = null,
    val basic_chk : Boolean? = null,
    val basic_price : Int? = null,
    val basic_sale_price : Int? = null,
    val basic_care_time : String? = null,
    val basic_standard_unit : String? = null,
    val membership1_chk : Boolean? = null,
    val membership1_price : Int? = null,
    val membership1_sale_price :  Int? = null,
    val membership1_care_time : String? = null,
    val membership1_standard_unit : String? = null,
    val membership2_chk : Boolean? = null,
    val membership2_price : Int? = null,
    val membership2_sale_price :  Int? = null,
    val membership2_care_time : String? = null,
    val membership2_standard_unit : String? = null,
    val option1 : String? = null,
    val option2 : String? = null,
    val option3 : String? = null,
    val option4 : String? = null,
    val score_avg_hospital : Float? = null,
    val score_avg_satisfaction : Float? = null,
    val score_avg_kindness : Float? = null,
    var review_count : Int? = null,
    val id : Int? = null,
    val code : String? = null,
    val main_img : String? = null,
    val checkup : ArrayList<HospitalCheckup>? = null,
    var isLastItem : Boolean = false
) : Serializable
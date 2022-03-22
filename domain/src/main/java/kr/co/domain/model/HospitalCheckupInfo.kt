package kr.co.domain.model

import java.io.Serializable

data class HospitalCheckupInfo(
    val id : Int,
    val hospital_id : Int,
    val hospital_checkup_id : Int,
    val title : String,
    val description : String,
    val sort : Int
) : Serializable
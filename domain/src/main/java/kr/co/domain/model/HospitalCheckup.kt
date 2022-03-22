package kr.co.domain.model

import java.io.Serializable

data class HospitalCheckup(
    val id : Int,
    val hospital_id : Int,
    val type : String,
    val checkup_process : String,
    val checkup_time : String,
    val checkup_device : String,
    val checkup_infos : ArrayList<HospitalCheckupInfo>
) : Serializable
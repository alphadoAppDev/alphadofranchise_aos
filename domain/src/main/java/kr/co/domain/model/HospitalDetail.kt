package kr.co.domain.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HospitalDetail(
    @SerializedName("basic")
    val hospitalInfo : HospitalInfo? = null,
    @SerializedName("working_hour")
    val workingTimes : ArrayList<WorkingHour>? = null,
    @SerializedName("image")
    var hospitalImages : ArrayList<HospitalImage>? = null,
    @SerializedName("checkup")
    val checkupInfo : ArrayList<HospitalCheckup>? = null,
    @SerializedName("medical_subject")
    val medicalSubjects : ArrayList<MedicalSubject>? = null,
    @SerializedName("medical_equipment")
    val medicalEquipments : ArrayList<MedicalEquipment>? = null
) : Serializable

//working_hour
data class WorkingHour(
    val day_of_week : Int? = null,
    val open_time : String? = null,
    val close_time : String? = null,
    val id : Int? = null,
    val hospital_id : Int? = null,
): Serializable

//image
data class HospitalImage(
    val type : String? = null,
    val url : String? = null,
    val id : Int? = null,
    val hospital_id: Int? = null,
    val is_del : Boolean? = null
): Serializable


//medical_subject
data class MedicalSubject(
    val title: String? = null,
    val price : Int? = null,
    val url : String? = null,
    val id : Int? = null,
    val hospital_id: Int? = null,
    val sort : Int? = null,
    var isLastItem : Boolean = false
): Serializable


//medical_equipment
data class MedicalEquipment(
    val title : String? = null,
    val description: String? = null,
    val id: Int? = null,
    val hospital_id: Int? = null,
    val sort: Int? = null,
    var isLastItem : Boolean = false
): Serializable
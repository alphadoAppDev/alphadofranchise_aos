package kr.co.domain

import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Single
import kr.co.domain.model.FranchiseInputCodeResponse

interface FranchiseRepository {
    fun requestInputStoreCode(code : String) : Single<JsonElement>
    fun requestRegionInfo(code : String) : Single<JsonElement>
    fun requestRegionInfo2(code : String) : Single<JsonElement>
    fun requestHeadOfficeInfo(code : String) : Single<JsonElement>

    fun requestFranchiseSummaryInfo(code : String) : Single<JsonElement>

}
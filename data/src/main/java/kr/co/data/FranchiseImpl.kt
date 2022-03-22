package kr.co.data

import android.util.Log
import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Single
import kr.co.domain.FranchiseRepository
import kr.co.domain.model.FranchiseInputCodeResponse
import kr.co.domain.model.FranchiseParameter

class FranchiseImpl(private val franchiseApi : FranchiseApi) : FranchiseRepository  {
    override fun requestInputStoreCode(code: String): Single<JsonElement> {

        val map = HashMap<String, String>()
        map["code"] = code
        return franchiseApi.requestInputStoreCode(map)

//        return franchiseApi.requestInputStoreCode(FranchiseParameter(code = code))
    }

    override fun requestRegionInfo(code: String): Single<JsonElement> {
//        val map = HashMap<String, String>()
//        map["code"] = code
//        return franchiseApi.requestRegionInfo(map)
        return franchiseApi.requestRegionInfo(FranchiseParameter(code = code))
    }

    override fun requestRegionInfo2(code: String): Single<JsonElement> {
        return franchiseApi.requestRegionInfo2(FranchiseParameter(code = code))
    }

    override fun requestHeadOfficeInfo(code: String): Single<JsonElement> {
        val map = HashMap<String, String>()
        map["code"] = code
        return franchiseApi.requestHeadOfficeInfo(map)
    }

    override fun requestFranchiseSummaryInfo(code: String): Single<JsonElement> {
        val map = HashMap<String, String>()
        map["code"] = code
        return franchiseApi.requestFranchiseSummaryInfo(map)
    }
}
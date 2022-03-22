package kr.co.domain

import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Single
import kr.co.domain.model.FranchiseInputCodeResponse

class FranchiseUseCase(private val franchiseRepository: FranchiseRepository) {
    fun executeInputStoreCode(code : String) : Single<JsonElement> =
        franchiseRepository.requestInputStoreCode(code = code)

    fun executeRegionInfo(code : String) : Single<JsonElement> =
        franchiseRepository.requestRegionInfo(code = code)

    fun executeRegionInfo2(code : String) : Single<JsonElement> =
        franchiseRepository.requestRegionInfo2(code = code)

    fun executeMonthProfitInfo(code : String) : Single<JsonElement> =
        franchiseRepository.requestHeadOfficeInfo(code = code)

    fun executeFranchiseSummaryInfo(code : String) : Single<JsonElement> =
        franchiseRepository.requestFranchiseSummaryInfo(code = code)
}
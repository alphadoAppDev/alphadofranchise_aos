package kr.co.alphadofranchise.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kr.co.alphadofranchise.models.FranchiseSummaryInfo
import kr.co.alphadopetshop.util.CommonUtils
import kr.co.alphadopetshop.util.Constants
import kr.co.domain.FranchiseUseCase
import kr.co.domain.model.*

class FranchiseViewModel(
    private val franchiseUseCase : FranchiseUseCase
) : ViewModel() {
    private val _inputCodeLiveData = MutableLiveData<Any?>()
    val inputCodeLiveData : LiveData<Any?> get() = _inputCodeLiveData

    private val _regionInfoLiveData = MutableLiveData<Any?>()
    val regionInfoLiveData : LiveData<Any?> get() = _regionInfoLiveData

    private val _profitInfoLiveData = MutableLiveData<Any?>()
    val profitInfoLiveData : LiveData<Any?> get() = _profitInfoLiveData

    private val _summaryInfoLiveData = MutableLiveData<Any?>()
    val summaryInfoLiveData : LiveData<Any?> get() = _summaryInfoLiveData


    fun requestInputStoreCode(code : String) {
        franchiseUseCase.executeInputStoreCode(code = code)
            .retry { retryCnt, _ ->
                CommonUtils.sleep(Constants.RETRY_DELAY)
                retryCnt < Constants.RETRY_MAX
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    val jsonObject = it.asJsonObject
                    val result = jsonObject.get("result").asString
                    val message = jsonObject.get("message").asString
                    val data = jsonObject.get("data")

                    if(result == "ok"){
                        _inputCodeLiveData.value = GsonBuilder().create().fromJson(data, StoreCode::class.java)
                    } else {
                        _inputCodeLiveData.value = message
                    }
                },
                onError = {
                    _inputCodeLiveData.value = null
                    it.printStackTrace()
                    Log.d("test","requestInputStoreCode error : ${it.localizedMessage}")
                }
            )
    }


    fun requestRegionInfo(code : String) {
        franchiseUseCase.executeRegionInfo(code = code)
            .retry { retryCnt, _ ->
                CommonUtils.sleep(Constants.RETRY_DELAY)
                retryCnt < Constants.RETRY_MAX
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    val jsonObject = it.asJsonObject
                    val result = jsonObject.get("result").asString
                    val message = jsonObject.get("message").asString
                    val data = jsonObject.get("data").asJsonArray

                    if(result == "ok"){
                        val list = ArrayList<FranchiseRegion>()

                        data.forEachIndexed { index, element ->
                            if(index == 0){
                                list.add(FranchiseRegion(
                                    date = (element.asJsonObject).get("date").asString,
                                    region = (element.asJsonObject).get("region").asString,
                                    title = (element.asJsonObject).get("title").asString,
                                    table = GsonBuilder().create().fromJson((element.asJsonObject).get("table"), RegionTable::class.java)
                                ))
                            } else if(index == 1 || index == 2 || index == 4 || index == 5){
                                list.add(FranchiseRegion(
                                    title = (element.asJsonObject).get("title").asString,
                                    table = GsonBuilder().create().fromJson((element.asJsonObject).get("table"), RegionTable::class.java)
                                ))
                            } else if(index == 3) {
                                list.add(FranchiseRegion(
                                    date = (element.asJsonObject).get("date").asString,
                                    title = (element.asJsonObject).get("title").asString,
                                    table = GsonBuilder().create().fromJson((element.asJsonObject).get("table"), RegionTable::class.java)
                                ))
                            }
                        }
                        _regionInfoLiveData.value = list
                    } else {
                        _regionInfoLiveData.value = message
                    }
                },
                onError = {
                    _regionInfoLiveData.value = null
                    it.printStackTrace()
                    Log.d("test","requestRegionInfo error : ${it.localizedMessage}")
                }
            )
    }

    fun requestMonthProfitInfo(code : String) {
        franchiseUseCase.executeMonthProfitInfo(code = code)
            .retry { retryCnt, _ ->
                CommonUtils.sleep(Constants.RETRY_DELAY)
                retryCnt < Constants.RETRY_MAX
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    val jsonObject = it.asJsonObject
                    val result = jsonObject.get("result").asString
                    val message = jsonObject.get("message").asString
                    val data = jsonObject.get("data").asJsonArray

                    if(result == "ok") {
                        val profitInfoList = ArrayList<ProfitInfo>()
                        data.forEach {
                            val profitInfo = GsonBuilder().create().fromJson(it, ProfitInfo::class.java)
                            profitInfoList.add(profitInfo)
                        }

                        _profitInfoLiveData.value = profitInfoList
                    } else {
                        _profitInfoLiveData.value = message
                    }
                },
                onError = {
                    _profitInfoLiveData.value = null
                    it.printStackTrace()
                    Log.d("test","requestHeadOfficeInfo error : ${it.localizedMessage}")
                }
            )
    }

    fun requestFranchiseSummaryInfo(code : String) {
        franchiseUseCase.executeFranchiseSummaryInfo(code = code)
            .retry { retryCnt, _ ->
                CommonUtils.sleep(Constants.RETRY_DELAY)
                retryCnt < Constants.RETRY_MAX
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    val jsonObject = it.asJsonObject
                    val result = jsonObject.get("result").asString
                    val message = jsonObject.get("message").asString
                    val data = jsonObject.get("data").asJsonArray

                    if(result == "ok") {
                        val summaryInfo = GsonBuilder().create().fromJson(data[0].asJsonObject, FranchiseSummaryInfo::class.java)
                        _summaryInfoLiveData.value = summaryInfo
                    } else {
                        _summaryInfoLiveData.value = message
                    }
                },
                onError = {
                    Log.d("test","requestFranchiseSummaryInfo error : ${it.localizedMessage}")
                    it.printStackTrace()
                    _summaryInfoLiveData.value = null
                }
            )
    }

    fun requestRegionInfo2(code : String) {
        franchiseUseCase.executeRegionInfo2(code = code)
            .retry { retryCnt, _ ->
                CommonUtils.sleep(Constants.RETRY_DELAY)
                retryCnt < Constants.RETRY_MAX
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    val jsonObject = it.asJsonObject
                    val result = jsonObject.get("result").asString
                    val message = jsonObject.get("message").asString
                    val data = jsonObject.get("data").asJsonArray

                    if(result == "ok"){
                        val list = ArrayList<FranchiseRegion>()

                        data.forEachIndexed { index, element ->
                            if(index == 0){
                                list.add(FranchiseRegion(
                                    date = (element.asJsonObject).get("date").asString,
                                    region = (element.asJsonObject).get("region").asString,
                                    title = (element.asJsonObject).get("title").asString,
                                    table = GsonBuilder().create().fromJson((element.asJsonObject).get("table"), RegionTable::class.java)
                                ))
                            } else if(index == 1 || index == 2 || index == 3 || index == 5 || index == 6){
                                list.add(FranchiseRegion(
                                    title = (element.asJsonObject).get("title").asString,
                                    table = GsonBuilder().create().fromJson((element.asJsonObject).get("table"), RegionTable::class.java)
                                ))
                            } else if(index == 4) {
                                list.add(FranchiseRegion(
                                    date = (element.asJsonObject).get("date").asString,
                                    title = (element.asJsonObject).get("title").asString,
                                    table = GsonBuilder().create().fromJson((element.asJsonObject).get("table"), RegionTable::class.java)
                                ))
                            }
                        }
                        _regionInfoLiveData.value = list
                    } else {
                        _regionInfoLiveData.value = message
                    }
                },
                onError = {
                    _regionInfoLiveData.value = null
                    it.printStackTrace()
                    Log.d("test","requestRegionInfo error : ${it.localizedMessage}")
                }
            )
    }

}
package kr.co.alphadopetshop

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kr.co.alphadopetshop.base.BaseViewModel
import kr.co.alphadopetshop.model.ReviewUpdateModel
import kr.co.alphadopetshop.util.CommonUtils
import kr.co.alphadopetshop.util.Constants
import kr.co.domain.HospitalUseCase
import kr.co.domain.model.*
import okhttp3.Callback
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File

class HospitalViewModel(
    private val hospitalUseCase: HospitalUseCase
) : BaseViewModel() {

    private val _allHospitalLiveData = MutableLiveData<Any?>()
    val allHospitalLiveData : LiveData<Any?> get() = _allHospitalLiveData

    private val _hospitalDetailInfoLiveData = MutableLiveData<Any?>()
    val hospitalDetailInfoLiveData : LiveData<Any?> get() = _hospitalDetailInfoLiveData

    private val _hospitalReviewLiveData = MutableLiveData<Any?>()
    val hospitalReviewLiveData : LiveData<Any?> get() = _hospitalReviewLiveData

    private val _reviewImageLiveData = MutableLiveData<ArrayList<String>>()
    val reviewImageLiveData : LiveData<ArrayList<String>> get() = _reviewImageLiveData

    private val _addReviewLiveData = MutableLiveData<Boolean?>()
    val addReviewLiveData : LiveData<Boolean?> get() = _addReviewLiveData

    private val _addReviewLikeLiveData = MutableLiveData<Any?>()
    val addReviewLikeLiveData : LiveData<Any?> get() = _addReviewLikeLiveData

    fun requestGetAllHospital(
        search_type : String?,
        search : String?,
        pos_lat : Float?,
        pos_long : Float?,
        display_level : String?,
        address : String?,
        premium_chk : String?,
        basic_chk : String?,
        is_show : Boolean?,
        is_sale : Boolean?,
        start_at : String?,
        end_at : String?,
        sort : String?,
        limit : Int?,
        page : Int?
    ){
        compositeDisposable.add(
            hospitalUseCase.executeGetAllHospital(
                search_type = search_type,
                search = search,
                pos_lat = pos_lat,
                pos_long  = pos_long,
                display_level = display_level,
                address = address,
                premium_chk = premium_chk,
                basic_chk = basic_chk,
                is_show = is_show,
                is_sale = is_sale,
                start_at = start_at,
                end_at = end_at,
                sort = sort,
                limit = limit,
                page = page
            )
                .retry { retryCnt, _ ->
                    CommonUtils.sleep(Constants.RETRY_DELAY)
                    retryCnt < Constants.RETRY_MAX
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgress() }
                .doAfterSuccess { hideProgress() }
                .subscribeBy(
                    onSuccess = {
                        if(it.isSuccessful){
                            val hospitalListResponse = GsonBuilder().create().fromJson(it.body(), HospitalListResponse::class.java)
                            Log.d("test","size : ${hospitalListResponse.data.size}")
                            _allHospitalLiveData.value = hospitalListResponse.data
                        } else {
                            _allHospitalLiveData.value =  GsonBuilder().create().fromJson(it.body(), Detail::class.java)
                        }
                    },
                    onError = {
                        _allHospitalLiveData.value = null
                        it.printStackTrace()
                        Log.d("test","requestGetAllHospital error : ${it.message}")
                    }
                )
        )
    }

    fun requestGetHospitalDetailInfo(hospital_id : Int){
        compositeDisposable.add(
            hospitalUseCase.executeGetHospitalDetailInfo(hospital_id = hospital_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgress() }
                .doAfterSuccess { hideProgress() }
                .subscribeBy(
                    onSuccess = {
                        if(it.isSuccessful){
                            val hospitalDetail = GsonBuilder().create().fromJson(it.body(), HospitalDetail::class.java)
                            _hospitalDetailInfoLiveData.value = hospitalDetail
                        } else {
                            _hospitalDetailInfoLiveData.value =  GsonBuilder().create().fromJson(it.body(), Detail::class.java)
                        }
                    },
                    onError = {
                        _hospitalDetailInfoLiveData.value = null
                        it.printStackTrace()
                        Log.d("test","requestGetHospitalDetailInfo error : ${it.message}")
                    },
                )
        )
    }

    fun initReviewLiveData(){
        _hospitalReviewLiveData.value = ArrayList<Any?>()
    }

    fun requestGetHospitalReviews(hospital_id: Int, page : Int, sort : String) {

        compositeDisposable.add(
            hospitalUseCase.executeGetHospitalReviews(hospital_id = hospital_id, page = page, sort = sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgress() }
                .doAfterSuccess { hideProgress() }
                .subscribeBy(
                    onSuccess = {
                        if(it.isSuccessful){
                            val hospitalReview = GsonBuilder().create().fromJson(it.body(), HospitalReview::class.java)

                            if(hospitalReviewLiveData.value == null) {
                                _hospitalReviewLiveData.value = hospitalReview.hospitalReviews
                            } else {
                                val list = hospitalReviewLiveData.value as java.util.ArrayList<HospitalReviewItem>

                                if(list.size > 0){
                                    hospitalReview.hospitalReviews.forEach { element -> list.add(element) }
                                    _hospitalReviewLiveData.value = list
                                } else {
                                    _hospitalReviewLiveData.value = hospitalReview.hospitalReviews
                                }
                            }
                        } else {
                            _hospitalReviewLiveData.value =  null
                        }
                    },
                    onError = {
                        _hospitalReviewLiveData.value = null
                        it.printStackTrace()
                        Log.d("test","requestGetHospitalReviews error : ${it.message}")
                    }
                )
        )
    }

    fun requestUploadImage(fileArray : ArrayList<File>){
        val urlList = ArrayList<String?>()

        for(file in fileArray) {
            compositeDisposable.add(
                hospitalUseCase.executeUploadImage(imageFile = file)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgress() }
                    .doAfterSuccess { hideProgress() }
                    .subscribeBy(
                        onSuccess = {
                            urlList.add(it.string())

                            if(fileArray.size == urlList.size) {
                                _reviewImageLiveData.value = removeNull(urlList)
                            }
                        },
                        onError = {
                            urlList.add(null)

                            if(fileArray.size == urlList.size)
                                _reviewImageLiveData.value = removeNull(urlList)

                            it.printStackTrace()
                            Log.d("test","requestUploadImage onError : ${it.message}")
                        }
                    )
            )
        }
    }

    fun requestAddReview(
        hospitalId : Int, writer : String, description : String,
        facilityScore : Int, examinationScore : Int, kindnessScore: Int,
        likeCount : Int, createdAt : String, urlList : ArrayList<String>
    ){
        compositeDisposable.add(
            hospitalUseCase.executeAddReview(
                hospitalId = hospitalId, writer=  writer, description = description, facilityScore = facilityScore,
                examinationScore = examinationScore, kindnessScore = kindnessScore, likeCount = likeCount, createdAt = createdAt, urlList = urlList
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgress() }
                .doAfterSuccess { hideProgress() }
                .subscribeBy(
                    onSuccess = {
                        _addReviewLiveData.value = true
                    },
                    onError = {
                        _addReviewLiveData.value = null
                        it.printStackTrace()
                        Log.d("test","requestAddReview error : ${it.message}")
                    }
                )
        )
    }

    private fun removeNull(list :  ArrayList<String?>) : ArrayList<String> {
        val removedList = ArrayList<String>()

        for(element in list) {
            if(element != null)
                removedList.add(element)
        }
        return removedList
    }

    fun requestAddReviewLike(review_id : Int, position : Int,  list : MutableList<HospitalReviewItem>){
        compositeDisposable.add(
            hospitalUseCase.executeAddReviewLike(review_id = review_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgress() }
                .doAfterSuccess { hideProgress() }
                .subscribeBy(
                    onSuccess = {
                        if(it.isSuccessful){
                            val hospitalAddReviewResponse = GsonBuilder().create().fromJson(it.body(), HospitalAddReviewResponse::class.java)
                            val reviewUpdateModel = ReviewUpdateModel(position = position, data = hospitalAddReviewResponse, list = list)
                            _addReviewLikeLiveData.value = reviewUpdateModel
                        } else {
                            _addReviewLikeLiveData.value =  GsonBuilder().create().fromJson(it.body(), Detail::class.java)
                        }
                    },
                    onError = {
                        _addReviewLikeLiveData.value = null
                        it.printStackTrace()
                        Log.d("test","requestAddReviewLike error : ${it.message}")
                    }
                )
        )
    }
}
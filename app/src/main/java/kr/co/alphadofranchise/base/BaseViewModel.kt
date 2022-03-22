package kr.co.alphadofranchise.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {
    protected val compositeDisposable = CompositeDisposable()
    val mIsLoading = MutableLiveData(false)

    fun showProgress() {
        mIsLoading.value = true
    }

    fun hideProgress() {
        mIsLoading.value = false
    }


    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
    }
}
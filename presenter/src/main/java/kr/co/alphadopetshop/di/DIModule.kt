package kr.co.alphadopetshop.di

import android.util.Log
import dagger.Module
import dagger.Provides
import kr.co.alphadopetshop.util.HttpString
import kr.co.data.HospitalApi
import kr.co.data.HospitalImpl
import kr.co.data.RetrofitClient
import kr.co.domain.HospitalUseCase

@Module
class DIModule {
    @Provides
    fun provideHospitalUseCase() : HospitalUseCase {
        return HospitalUseCase(HospitalImpl(RetrofitClient.getClient(HttpString.SHOP_URL_DEV)?.create(HospitalApi::class.java)!!))
    }
}
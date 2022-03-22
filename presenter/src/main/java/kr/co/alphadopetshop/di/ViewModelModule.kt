package kr.co.alphadopetshop.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import kr.co.alphadopetshop.HospitalViewModel
import kr.co.alphadopetshop.util.HttpString
import kr.co.data.HospitalApi
import kr.co.data.HospitalImpl
import kr.co.data.RetrofitClient
import kr.co.domain.HospitalUseCase
import kotlin.reflect.KClass

@Module
class ViewModelModule {
    @MapKey
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER
    )
    annotation class ViewModelKey(val value : KClass<out ViewModel>)

    @Provides
    @IntoMap
    @ViewModelKey(HospitalViewModel::class)
    fun providesHospitalViewModel() : ViewModel {
        return HospitalViewModel(
            HospitalUseCase(
                HospitalImpl(RetrofitClient.getClient(HttpString.SHOP_URL_DEV)?.create(HospitalApi::class.java)!!)
            )
        )
    }
}
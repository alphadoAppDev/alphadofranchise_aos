package kr.co.alphadofranchise.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import kr.co.alphadofranchise.viewmodel.FranchiseViewModel
import kr.co.alphadopetshop.util.HttpString
import kr.co.data.*
import kr.co.domain.FranchiseUseCase
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
    @ViewModelKey(FranchiseViewModel::class)
    fun providesFranchiseViewModel() : ViewModel {
        return FranchiseViewModel(
            FranchiseUseCase(
                FranchiseImpl(FranchiseRetrofitClient.getClient(HttpString.FRANCHISE_URL_DEV)?.create(FranchiseApi::class.java)!!)
            )
        )
    }
}
package kr.co.alphadofranchise.di

import dagger.Module
import dagger.Provides
import kr.co.alphadopetshop.util.HttpString
import kr.co.data.*
import kr.co.domain.FranchiseUseCase
import kr.co.domain.HospitalUseCase

@Module
class DIModule {
    @Provides
    fun provideFranchiseUseCase() : FranchiseUseCase {
        return FranchiseUseCase(
            FranchiseImpl(
                FranchiseRetrofitClient.getClient(HttpString.FRANCHISE_URL_DEV)?.create(
                    FranchiseApi::class.java)!!)
        )
    }
}
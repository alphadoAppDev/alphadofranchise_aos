package kr.co.alphadofranchise.di

import dagger.Component
import kr.co.alphadopetshop.BaseActivity
import kr.co.alphadopetshop.BaseFragment
import kr.co.alphadofranchise.di.ViewModelModule
import kr.co.alphadopetshop.di.ViewModelFactoryModule

@Component(modules = [DIModule::class, ViewModelModule::class, ViewModelFactoryModule::class])
interface DIComponent {
    fun injectBaseActivity(activity : kr.co.alphadofranchise.base.BaseActivity)
    fun injectBaseFragment(activity : kr.co.alphadofranchise.base.BaseFragment)
}
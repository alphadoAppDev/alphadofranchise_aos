package kr.co.alphadopetshop.di

import dagger.Component
import kr.co.alphadopetshop.BaseActivity
import kr.co.alphadopetshop.BaseFragment

@Component(modules = [DIModule::class, ViewModelModule::class, ViewModelFactoryModule::class])
interface DIComponent {
    fun injectBaseActivity(activity : BaseActivity)
    fun injectBaseFragment(activity : BaseFragment)
}
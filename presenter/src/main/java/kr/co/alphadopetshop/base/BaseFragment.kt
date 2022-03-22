package kr.co.alphadopetshop

import androidx.fragment.app.Fragment

import io.reactivex.rxjava3.disposables.CompositeDisposable
import kr.co.alphadopetshop.di.DIModule
import kr.co.alphadopetshop.di.DaggerDIComponent
import kr.co.alphadopetshop.di.ViewModelFactory
import javax.inject.Inject


open class BaseFragment : Fragment() {
    val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    init {
        DaggerDIComponent.builder()
            .build()
            .injectBaseFragment(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }

}
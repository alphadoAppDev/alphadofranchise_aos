package kr.co.alphadopetshop

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

import io.reactivex.rxjava3.disposables.CompositeDisposable
import kr.co.alphadopetshop.di.DIModule
import kr.co.alphadopetshop.di.DaggerDIComponent
import kr.co.alphadopetshop.di.ViewModelFactory
import javax.inject.Inject


open class BaseActivity : AppCompatActivity() {
    val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    init {
        DaggerDIComponent.builder()
            .build()
            .injectBaseActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

    }


    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }

}
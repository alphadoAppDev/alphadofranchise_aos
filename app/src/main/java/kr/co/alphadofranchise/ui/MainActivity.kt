package kr.co.alphadofranchise.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.navigation.NavigationBarView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kr.co.alphadofranchise.R
import kr.co.alphadofranchise.databinding.ActivityMainBinding
import kr.co.alphadofranchise.base.BaseActivity
import kr.co.alphadofranchise.ui.tab00.FranchiseFragment
import kr.co.alphadopetshop.ui.ShopMainFragment
import kr.co.alphadopetshop.util.Constants
import kr.co.data.db.LocalPreference

class MainActivity : BaseActivity() {
    private var binding : ActivityMainBinding? = null
    private var storeFragment : StoreFragment? = null
    private var franchiseFramgent : FranchiseFragment? = null
    private var shopMainFragment : ShopMainFragment? = null
    private var prepareFragment1: PrepareFragment? = null
    private var prepareFragment2: PrepareFragment? = null
    private var moreFragment: MoreFragment? = null
    private var storeCode : String? = null
    private var region : String? = null
    private var isShopLoaded  : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()

        if(storeCode == "-1") {
            Toast.makeText(applicationContext, "오류가 발생하였습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
        } else {
            binding!!.bottomNaviView.setOnItemSelectedListener(onMenuSelectedListener)
            Handler(Looper.getMainLooper()).postDelayed({
                supportFragmentManager.beginTransaction().add(R.id.container, shopMainFragment!!).commit()
                supportFragmentManager.beginTransaction().add(R.id.container, franchiseFramgent!!).commit()
            }, 300)
        }
    }

    private fun init(){
        storeCode = intent.getStringExtra("code")!!
        region = intent.getStringExtra("region")!!
        if(storeCode != null && region != null) franchiseFramgent =
            FranchiseFragment.newInstance(storeCode!!, region!!)
        if(storeCode != null && region != null) shopMainFragment = ShopMainFragment.newInstance()
    }

    fun visibleProgressBar(isVisible : Boolean) {
        binding!!.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private val onMenuSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.menu0 -> {
                    if(franchiseFramgent == null) {
                        franchiseFramgent = FranchiseFragment.newInstance(storeCode!!, region!!)
                        supportFragmentManager.beginTransaction().add(R.id.container, franchiseFramgent!!).commit()
                    }
                    if(franchiseFramgent != null) supportFragmentManager.beginTransaction().show(franchiseFramgent!!).commit()
                    if(shopMainFragment != null) supportFragmentManager.beginTransaction().hide(shopMainFragment!!).commit()
                    if(prepareFragment1 != null) supportFragmentManager.beginTransaction().hide(prepareFragment1!!).commit()
                    if(prepareFragment2 != null) supportFragmentManager.beginTransaction().hide(prepareFragment2!!).commit()
                    if(moreFragment != null) supportFragmentManager.beginTransaction().hide(moreFragment!!).commit()


                }
                R.id.menu1 -> {
//                    if(!isShopLoaded) {
//                        supportFragmentManager.beginTransaction().add(R.id.container, shopMainFragment!!).commit()
//                        isShopLoaded = true
//                    }

                    val listener = object : PermissionListener {
                        override fun onPermissionGranted() {
                            shopMainFragment?.requestHospitalInfo()
                            LocalPreference.setIsInitial(Constants.TYPE_GRANTED, applicationContext)
                        }
                        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                            shopMainFragment?.requestHospitalInfo()
                            LocalPreference.setIsInitial(Constants.TYPE_DENIED, applicationContext)
                        }
                    }

                    if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                        if(LocalPreference.getInstance().getIsInitial(applicationContext) == Constants.TYPE_NOR_SELECTED) {
                            TedPermission.with(applicationContext)
                                .setPermissionListener(listener)
                                .setDeniedMessage(getString(kr.co.alphadopetshop.R.string.permisson_location_denied))
                                .setPermissions(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ).check()
                        }
                    }

                    if(franchiseFramgent != null) supportFragmentManager.beginTransaction().hide(franchiseFramgent!!).commit()
                    if(shopMainFragment != null) supportFragmentManager.beginTransaction().show(shopMainFragment!!).commit()
                    if(prepareFragment1 != null) supportFragmentManager.beginTransaction().hide(prepareFragment1!!).commit()
                    if(prepareFragment2 != null) supportFragmentManager.beginTransaction().hide(prepareFragment2!!).commit()
                    if(moreFragment != null) supportFragmentManager.beginTransaction().hide(moreFragment!!).commit()
                }
                R.id.menu2 -> {
                    if(prepareFragment1 == null) {
                        prepareFragment1 = PrepareFragment.newInstance(1)
                        supportFragmentManager.beginTransaction().add(R.id.container, prepareFragment1!!).commit()
                    }
                    if(franchiseFramgent != null) supportFragmentManager.beginTransaction().hide(franchiseFramgent!!).commit()
                    if(shopMainFragment != null) supportFragmentManager.beginTransaction().hide(shopMainFragment!!).commit()
                    if(prepareFragment1 != null) supportFragmentManager.beginTransaction().show(prepareFragment1!!).commit()
                    if(prepareFragment2 != null) supportFragmentManager.beginTransaction().hide(prepareFragment2!!).commit()
                    if(moreFragment != null) supportFragmentManager.beginTransaction().hide(moreFragment!!).commit()
                }
                R.id.menu3 -> {
                    if(prepareFragment2 == null) {
                        prepareFragment2 = PrepareFragment.newInstance(2)
                        supportFragmentManager.beginTransaction().add(R.id.container, prepareFragment2!!).commit()
                    }
                    if(franchiseFramgent != null) supportFragmentManager.beginTransaction().hide(franchiseFramgent!!).commit()
                    if(shopMainFragment != null) supportFragmentManager.beginTransaction().hide(shopMainFragment!!).commit()
                    if(prepareFragment1 != null) supportFragmentManager.beginTransaction().hide(prepareFragment1!!).commit()
                    if(prepareFragment2 != null) supportFragmentManager.beginTransaction().show(prepareFragment2!!).commit()
                    if(moreFragment != null) supportFragmentManager.beginTransaction().hide(moreFragment!!).commit()
                }
                R.id.menu4 -> {
                    if(moreFragment == null) {
                        moreFragment = MoreFragment.newInstance()
                        supportFragmentManager.beginTransaction().add(R.id.container, moreFragment!!).commit()
                    }
                    if(franchiseFramgent != null) supportFragmentManager.beginTransaction().hide(franchiseFramgent!!).commit()
                    if(shopMainFragment != null) supportFragmentManager.beginTransaction().hide(shopMainFragment!!).commit()
                    if(prepareFragment1 != null) supportFragmentManager.beginTransaction().hide(prepareFragment1!!).commit()
                    if(prepareFragment2 != null) supportFragmentManager.beginTransaction().hide(prepareFragment2!!).commit()
                    if(moreFragment != null) supportFragmentManager.beginTransaction().show(moreFragment!!).commit()
                }
            }
            true
        }



    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}
package kr.co.alphadopetshop.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kr.co.alphadopetshop.BaseFragment
import kr.co.alphadopetshop.HospitalViewModel
import kr.co.alphadopetshop.ImageLoadThread
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.FragmentShopMainBinding
import kr.co.alphadopetshop.databinding.ItemHospitalListBinding
import kr.co.alphadopetshop.ui.health_check.HospitalDetailActivity
import kr.co.alphadopetshop.ui.main.HealthCheckFilterActivity
import kr.co.alphadopetshop.ui.main.HealthCheckFragment
import kr.co.alphadopetshop.util.CommonUtils
import kr.co.alphadopetshop.util.Constants
import kr.co.alphadopetshop.util.FlowLayout
import kr.co.data.db.LocalPreference
import kr.co.domain.model.Detail
import kr.co.domain.model.HospitalInfo
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.security.MessageDigest
import java.text.DecimalFormat
import kotlin.math.roundToInt


class ShopMainFragment : BaseFragment(), View.OnClickListener, OnHealthCheckItemListener,
    MapView.POIItemEventListener, MapView.MapViewEventListener {
    private var binding : FragmentShopMainBinding? = null
    private var adapter: HealthCheckAdapter? = null
    private var currentSelectedHospital: Int? = null
    private lateinit var hospitalViewModel: HospitalViewModel
    private var isShowMap: Boolean = false
    private var isInitial = true
    private var isRunFiltering : Boolean = false
    private lateinit var mGlideRequestManager: RequestManager




    companion object { @JvmStatic fun newInstance() = ShopMainFragment() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_shop_main, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        if(LocalPreference.getInstance().getIsInitial(requireContext()) != Constants.TYPE_NOR_SELECTED) {
            requestHospitalInfo()
        }



//        val listener = object : PermissionListener {
//            override fun onPermissionGranted() {
//                requestHospitalInfo()
//                LocalPreference.setIsInitial(Constants.TYPE_GRANTED, requireContext())
//            }
//            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
//                requestHospitalInfo()
//                LocalPreference.setIsInitial(Constants.TYPE_DENIED, requireContext())
//            }
//        }
//
//        if(ContextCompat.checkSelfPermission(requireActivity().applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED) {
//
//                if(LocalPreference.getInstance().getIsInitial(requireContext()) == Constants.TYPE_NOR_SELECTED) {
//                    TedPermission.with(requireContext())
//                        .setPermissionListener(listener)
//                        .setDeniedMessage(getString(R.string.permisson_location_denied))
//                        .setPermissions(
//                            Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_COARSE_LOCATION
//                        ).check()
//                } else {
//                    requestHospitalInfo()
//                }
//
//        } else {
//            if(!isRunFiltering)
//                requestHospitalInfo()
//        }
        setRecyclerView()
        observeHospitalInfo()
    }


    private fun observeHospitalInfo() {
        hospitalViewModel.allHospitalLiveData.observe(viewLifecycleOwner, {
            if (it != null) {
                binding!!.mapView.removeAllViews()

                if (it is Detail) {
                    Toast.makeText(requireActivity().applicationContext, it.msg, Toast.LENGTH_SHORT).show()
                    isRunFiltering = false
                } else {
                    val hospitalList = it as ArrayList<HospitalInfo>
                    if (hospitalList.size > 0) { hospitalList[hospitalList.size - 1].isLastItem = true }
                    binding!!.txtItemCount.text = "전체 ${hospitalList.size}개"

                    adapter!!.submitList(null)
                    adapter!!.submitList(hospitalList.toList()) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            binding!!.layoutIntro.visibility = View.GONE
                            if(hospitalList.size > 0) {
                                setMap(hospitalList[0], hospitalList)
                                currentSelectedHospital = 0
                            } else {
                                currentSelectedHospital = null
                            }
                            isRunFiltering = false
                        },1000)
                    }
                }
            } else {
                Toast.makeText(requireActivity().applicationContext, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
                isRunFiltering = false
            }
        })
    }

    private fun init() {
        LocalPreference.getInstance().init(requireActivity().applicationContext)
        LocalPreference.getInstance().setFilter1("",requireActivity().applicationContext)
        LocalPreference.getInstance().setFilter2("",requireActivity().applicationContext)
        LocalPreference.getInstance().setFilter3("",requireActivity().applicationContext)
        LocalPreference.getInstance().setFilter4("",requireActivity().applicationContext)
        LocalPreference.getInstance().setFilter5("",requireActivity().applicationContext)

        binding!!.viewPager.isUserInputEnabled = false
        mGlideRequestManager = Glide.with(this)

        binding!!.btnFilter.setOnClickListener(this)
        binding!!.btnMap.setOnClickListener(this)
        binding!!.bottomView.setOnClickListener(this)
        hospitalViewModel =
            ViewModelProvider(this, viewModelFactory).get(HospitalViewModel::class.java)

//        binding!!.layoutTab.bringToFront()
        binding!!.stickyScrollView.isHeaderTwo = false
        isInitial = true
    }

    private fun setRecyclerView() {
        adapter = HealthCheckAdapter(this)
        binding!!.recyclerHealthCheck.adapter = adapter
    }

//    private fun setTabLayout() {
//
//        binding!!.viewPager.apply { adapter = PagerAdapter(requireActivity(), 3) }
//
//        TabLayoutMediator(binding!!.tabLayout, binding!!.viewPager) { tab, position ->
//            when (position) {
//                0 -> tab.text = "건강검진"
//                1 -> tab.text = "미용"
//                2 -> tab.text = "쇼핑"
//            }
//        }.attach()
//
//        binding!!.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabUnselected(tab: TabLayout.Tab?) {}
//            override fun onTabReselected(tab: TabLayout.Tab?) {}
//
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                when (tab?.position) {
//                    0 -> {
//                        binding!!.recyclerHealthCheck.visibility = View.VISIBLE
//                        binding!!.layoutMenu.visibility = View.VISIBLE
//                        binding!!.underLine.visibility = View.VISIBLE
//                        binding!!.viewPager.visibility = View.GONE
//
//                        if (isShowMap)
//                            binding!!.layoutMap.visibility = View.VISIBLE
//
//                    }
//                    1 -> {
//                        binding!!.recyclerHealthCheck.visibility = View.GONE
//                        binding!!.layoutMenu.visibility = View.GONE
//                        binding!!.underLine.visibility = View.GONE
//                        binding!!.viewPager.visibility = View.VISIBLE
//
//                        if (isShowMap)
//                            binding!!.layoutMap.visibility = View.GONE
//                    }
//                    2 -> {
//                        binding!!.recyclerHealthCheck.visibility = View.GONE
//                        binding!!.layoutMenu.visibility = View.GONE
//                        binding!!.underLine.visibility = View.GONE
//                        binding!!.viewPager.visibility = View.VISIBLE
//
//                        if (isShowMap)
//                            binding!!.layoutMap.visibility = View.GONE
//                    }
//                }
//            }
//
//        })
//    }

    fun requestHospitalInfo(){
        hospitalViewModel.requestGetAllHospital(
            search_type = null,
            search = null,
            pos_lat = null,
            pos_long = null,
            display_level = null,
            address = null,
            premium_chk = null,
            basic_chk = null,
            is_show = true,
            is_sale = true,
            start_at = null,
            end_at = null,
            sort = null,
            limit = null,
            page = null
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnFilter -> {
                val intent = Intent(requireContext(), HealthCheckFilterActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                filterLauncher.launch(intent)
            }
            R.id.btnMap -> {
                val hospitalList = adapter!!.currentList.toMutableList()
                if (hospitalList.size > 0) {
                    if (binding!!.layoutMap.visibility == View.GONE) {
                        binding!!.bottomView.visibility = View.VISIBLE
                        binding!!.layoutMap.visibility = View.VISIBLE
                        binding!!.recyclerHealthCheck.visibility = View.GONE
                        binding!!.btnMap.setImageResource(R.drawable.icon_home_list_gray)
                        binding!!.layoutLogo.visibility = View.GONE
                        binding!!.layoutLogoInvisible.visibility = View.GONE
                        isShowMap = true
                    } else {
                        binding!!.recyclerHealthCheck.visibility = View.VISIBLE
                        binding!!.layoutMap.visibility = View.GONE
                        binding!!.btnMap.setImageResource(R.drawable.icon_home_map_gray)
                        binding!!.layoutLogo.visibility = View.VISIBLE
                        binding!!.layoutLogoInvisible.visibility = View.INVISIBLE
                        isShowMap = false
                    }
                }
            }
            R.id.bottomView -> {
                val hospitalList = adapter!!.currentList.toMutableList()
                if (currentSelectedHospital != null) {
                    binding!!.mapView.removeAllViews()

                    val intent = Intent(requireContext(), HospitalDetailActivity::class.java)
                    intent.putExtra("hospital_id", hospitalList[currentSelectedHospital!!].id)
                    intent.putExtra("position", currentSelectedHospital)
                    hospitalDetailLauncher.launch(intent)
                }
            }
        }
    }

    private val filterLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Constants.RESULT_CODE_FILTER) {
                //address : String
                //premium, basic : String, 전체 선택시 둘 다 null
                //latitude, longitude : Float, 필터 선택 안했을 시 null
                //review_score : String, 필터 선택 안했을 시 null
                //review_count : String, 필터 선택 안했을 시 null


                if (it.data != null) {
                    isRunFiltering = true
                    Log.d("test", "333")
                    val address = it.data!!.getStringExtra("address")
                    val premium = it.data!!.getStringExtra("premium")
                    val basic = it.data!!.getStringExtra("basic")
                    var latitude: Float? = it.data!!.getFloatExtra("latitude", 0.0f)
                    var longitude: Float? = it.data!!.getFloatExtra("longitude", 0.0f)
                    val review_score = it.data!!.getStringExtra("review_score")
                    val review_count = it.data!!.getStringExtra("review_count")

                    if (latitude == 0.0f)
                        latitude = null
                    if (longitude == 0.0f) {
                        longitude = null
                    }

                    var sort: String? = null
                    if (latitude != null && longitude != null) {
                        sort = null
                    } else if (review_score != null) {
                        sort = review_score
                    } else if (review_count != null) {
                        sort = review_count
                    }

                    Log.d("test","address : $address")


                    hospitalViewModel.requestGetAllHospital(
                        search_type = null,
                        search = null,
                        pos_lat = latitude,
                        pos_long = longitude,
                        display_level = null,
                        address = address,
                        premium_chk = premium,
                        basic_chk = basic,
                        is_show = true,
                        is_sale = true,
                        start_at = null,
                        end_at = null,
                        sort = sort,
                        limit = null,
                        page = null
                    )
                }
            }
        }

    override fun onHealthCheckItemClicked(item: HospitalInfo, position : Int) {
        binding!!.mapView.removeAllViews()

        val intent = Intent(requireContext(), HospitalDetailActivity::class.java)
        intent.putExtra("hospital_id", item.id)
        intent.putExtra("position",position)
        hospitalDetailLauncher.launch(intent)
    }

    private fun setMap(hospitalInfo: HospitalInfo, list: MutableList<HospitalInfo>) {
        try{
            val mapView = MapView(requireContext())
            mapView.setMapCenterPoint(
                MapPoint.mapPointWithGeoCoord(
                    hospitalInfo.addr_lat!!.toDouble(),
                    hospitalInfo.addr_long!!.toDouble()
                ), true
            )

            for (i in 0 until list.size) {
                val marker = MapPOIItem()
                marker.apply {
                    itemName = list[i].name
                    tag = i
                    mapPoint = MapPoint.mapPointWithGeoCoord(
                        list[i].addr_lat!!.toDouble(),
                        list[i].addr_long!!.toDouble()
                    )
                    markerType = MapPOIItem.MarkerType.CustomImage
                    customImageResourceId = R.drawable.icon_location_purple_18
                    selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                    customSelectedImageResourceId = R.drawable.icon_location_purple_24

                }
                mapView.addPOIItem(marker)
            }

            val frameLayout = FrameLayout(requireContext())
            frameLayout.layoutParams =
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            frameLayout.addView(mapView)
            binding!!.mapView.addView(frameLayout)

            mapView.setPOIItemEventListener(this)
            mapView.setMapViewEventListener(this)

        }catch (e: Exception){ e.printStackTrace() }

        setBottomView(hospitalInfo)
    }

    private fun setBottomView(hospitalInfo: HospitalInfo) {
        val decimalFormatter = DecimalFormat("###,###");

        mGlideRequestManager
            .load(Uri.parse(hospitalInfo.main_img))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(binding!!.imgHospital)


        if (hospitalInfo.basic_chk == true && hospitalInfo.premium_chk == true) {
            binding!!.txtRank.text =
                "${getString(R.string.membership_rank00)}, ${getString(R.string.membership_rank01)}"
        } else {
            if (hospitalInfo.basic_chk == true) {
                binding!!.txtRank.text = "${getString(R.string.membership_rank00)}"
            } else {
                binding!!.txtRank.text = "${getString(R.string.membership_rank01)}"
            }
        }
        val grade =
            (hospitalInfo.score_avg_hospital!! + hospitalInfo.score_avg_satisfaction!! + hospitalInfo.score_avg_kindness!!) / 3
        binding!!.txtReviewScore.text = "${(grade * 10).roundToInt() / 10.0f}"
        binding!!.txtReviewCnt.text = "(${decimalFormatter.format(hospitalInfo.review_count)})"
        binding!!.txtHospitalName.text = hospitalInfo.name

        try {
            var distance = CommonUtils.calculateDistance(
                requireContext(),
                hospitalInfo.addr_lat!!.toDouble(),
                hospitalInfo.addr_long!!.toDouble()
            )
            if (Math.round(distance.toDouble()).toString().length >= 4) {
                distance =
                    (Math.round((distance.toDouble() / 1000) * 100) / 100.0).toString() + "km"
            } else {
                distance = Math.round(distance.toDouble()).toString() + "m"
            }
            binding!!.txtHospitalInfo.text = "$distance, ${hospitalInfo.address}"
        } catch (e: Exception) {
            binding!!.txtHospitalInfo.text = "${hospitalInfo.address}"
        }


        if (hospitalInfo.premium_chk == true) {
            binding!!.txtPriceRank.text = "${getString(R.string.membership_rank_price01)}"
            binding!!.txtPrice.text =
                "${decimalFormatter.format(hospitalInfo.premium_price)}"
        } else {
            binding!!.txtPriceRank.text = "${getString(R.string.membership_rank_price00)}"
            binding!!.txtPrice.text =
                "${decimalFormatter.format(hospitalInfo.basic_price)}"
        }


        binding!!.layoutFlow.removeAllViews()
        if (hospitalInfo.checkup != null) {
            for (s in hospitalInfo.checkup!!) {
                val type = CommonUtils.transLanguageHospitalType(s.type, requireContext())

                val tv = TextView(requireContext())
                tv.text = type
                tv.setTextColor(requireContext().getColor(R.color.font_title))
                tv.setBackgroundResource(R.drawable.bg_10dp_rounding_point_sub)
                tv.textSize = 12f
                tv.includeFontPadding = false
                tv.setPadding(20, 10, 20, 10)
                val params = FlowLayout.LayoutParams(10, 10)
                tv.layoutParams = params
                tv.layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                binding!!.layoutFlow.addView(tv)
            }
        }
    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        setBottomView(adapter!!.currentList[p1!!.tag])
        currentSelectedHospital = p1.tag
        binding!!.bottomView.visibility = View.VISIBLE
    }



    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {}

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
        if (currentSelectedHospital != null) {
            binding!!.mapView.removeAllViews()

            val intent = Intent(requireContext(), HospitalDetailActivity::class.java)
            intent.putExtra("hospital_id", adapter!!.currentList[currentSelectedHospital!!].id)
            intent.putExtra("position",currentSelectedHospital)
            hospitalDetailLauncher.launch(intent)
        }
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}
    override fun onMapViewInitialized(p0: MapView?) {}
    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {}

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        if (binding!!.bottomView.visibility == View.VISIBLE) {
            binding!!.bottomView.visibility = View.GONE
        } else {
            binding!!.bottomView.visibility = View.VISIBLE
        }
    }

    private val hospitalDetailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Constants.RESULT_CODE_HOSPITAL_DETAIL) {
                val hospitalList = adapter!!.currentList.toMutableList()
                Log.d("test","!!!")
                it.data.let {
                    try{
                        val reviewCount = it!!.getIntExtra("review_count",0)
                        val position = it.getIntExtra("position",0)

                        val newList = mutableListOf<HospitalInfo>()
                        newList.addAll(hospitalList)
                        newList[position] = HospitalInfo(
                            name = hospitalList[position].name, tel = hospitalList[position].tel,
                            mainDoctor = hospitalList[position].mainDoctor, introduce1 = hospitalList[position].introduce1,
                            introduce2 = hospitalList[position].introduce2, introduce3 = hospitalList[position].introduce3,
                            address = hospitalList[position].address, addr_lat = hospitalList[position].addr_lat,
                            addr_long = hospitalList[position].addr_long, enable_parking = hospitalList[position].enable_parking,
                            directions = hospitalList[position].directions, holiday_open_time = hospitalList[position].holiday_open_time,
                            holiday_close_time = hospitalList[position].holiday_close_time, lunch_start_time = hospitalList[position].lunch_start_time,

                            lunch_end_time = hospitalList[position].lunch_end_time, premium_chk = hospitalList[position].premium_chk,
                            premium_price = hospitalList[position].premium_price, premium_sale_price = hospitalList[position].premium_sale_price,
                            premium_care_time = hospitalList[position].premium_care_time, premium_standard_unit = hospitalList[position].premium_standard_unit,
                            basic_chk = hospitalList[position].basic_chk, basic_price = hospitalList[position].basic_price,
                            basic_sale_price = hospitalList[position].basic_sale_price, basic_care_time = hospitalList[position].basic_care_time,
                            basic_standard_unit = hospitalList[position].basic_standard_unit, membership1_chk = hospitalList[position].membership1_chk,

                            membership1_price = hospitalList[position].membership1_price, membership1_sale_price = hospitalList[position].membership1_sale_price,
                            membership1_care_time = hospitalList[position].membership1_care_time, membership1_standard_unit = hospitalList[position].membership1_standard_unit,
                            membership2_chk = hospitalList[position].membership2_chk, membership2_price = hospitalList[position].membership2_price,
                            membership2_sale_price = hospitalList[position].membership2_sale_price, membership2_care_time = hospitalList[position].membership2_care_time,
                            membership2_standard_unit = hospitalList[position].membership2_standard_unit, option1 = hospitalList[position].option1,

                            option2 = hospitalList[position].option2, option3 = hospitalList[position].option3,
                            option4 = hospitalList[position].option4, score_avg_hospital = hospitalList[position].score_avg_hospital,
                            score_avg_satisfaction = hospitalList[position].score_avg_satisfaction, score_avg_kindness = hospitalList[position].score_avg_kindness,
                            review_count = reviewCount, id = hospitalList[position].id,
                            code = hospitalList[position].code, main_img = hospitalList[position].main_img,
                            checkup = hospitalList[position].checkup, isLastItem = hospitalList[position].isLastItem,
                        )


                        adapter!!.submitList(newList.toList()){
                            try {
                                if (binding!!.mapView.getChildAt(0) == null) {
                                    if (newList.size > 0 && currentSelectedHospital != null) {
                                        setMap(newList[currentSelectedHospital!!], newList)
                                    }
                                }
                            }  catch (e: Exception) {
                                currentSelectedHospital = null
                                Log.d("test", "onResume mapView error : ${e.message}")
                            }
                        }
                    }catch (e: java.lang.Exception) {e.printStackTrace()}

                }

            }
        }
}

class PagerAdapter(
    fa: FragmentActivity,
    private val pageNumber: Int
) : FragmentStateAdapter(fa) {


    override fun getItemCount(): Int = pageNumber

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            else -> HealthCheckFragment.newInstance()
//            1 -> BeautyTreatmentFragment.newInstance()
//            else -> ShoppingFragment.newInstance()
        }
    }
}

interface OnHealthCheckItemListener {
    fun onHealthCheckItemClicked(item: HospitalInfo, position: Int)
}

class HealthCheckAdapter(
    private val listener: OnHealthCheckItemListener
) : ListAdapter<HospitalInfo, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<HospitalInfo>(){
        override fun areItemsTheSame(oldItem: HospitalInfo, newItem: HospitalInfo): Boolean {
            Log.d("test","11oldItem : ${oldItem.review_count}")
            Log.d("test","11newItem : ${newItem.review_count}")
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HospitalInfo, newItem: HospitalInfo): Boolean {
            Log.d("test","22oldItem : ${oldItem.review_count}")
            Log.d("test","22newItem : ${newItem.review_count}")
            return oldItem.review_count == newItem.review_count
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var binding = DataBindingUtil.inflate<ItemHospitalListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_hospital_list,
            parent,
            false
        )
        return HealthCheckViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HealthCheckViewHolder).bind(getItem(position))
    }


    override fun submitList(list: List<HospitalInfo?>?) {
        super.submitList(list?.let { ArrayList(it) })


    }

    class HealthCheckViewHolder(
        private val binding: ItemHospitalListBinding,
        private val listener: OnHealthCheckItemListener
    ) : RecyclerView.ViewHolder(binding.root) {
        private val mHandler = object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)

                val bundle = msg.data
                val url : String? = bundle.getString("url")

                url?.let {
                    Glide.with(binding.root.context)
                        .load(Uri.parse(url))
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(binding.imgHospital)
                }
            }
        }


        fun bind(hospitalInfo: HospitalInfo) {
            val decimalFormatter = DecimalFormat("###,###");
            val context = binding.imgHospital.context
            binding.topView.setOnClickListener { listener.onHealthCheckItemClicked(hospitalInfo, adapterPosition) }

            if (hospitalInfo.isLastItem) {
                binding.divider.visibility = View.INVISIBLE
            } else {
                binding.divider.visibility = View.VISIBLE
            }

            ImageLoadThread(mHandler, hospitalInfo.main_img).start()

            if (hospitalInfo.basic_chk == true && hospitalInfo.premium_chk == true) {
                binding.txtRank.text = "${binding.root.context.getString(R.string.membership_rank00)}, ${binding.root.context.getString(R.string.membership_rank01)}"
            } else {
                if (hospitalInfo.basic_chk == true) {
                    binding.txtRank.text =
                        "${binding.root.context.getString(R.string.membership_rank00)}"
                } else {
                    binding.txtRank.text =
                        "${binding.root.context.getString(R.string.membership_rank01)}"
                }
            }

            val grade =
                (hospitalInfo.score_avg_hospital!! + hospitalInfo.score_avg_satisfaction!! + hospitalInfo.score_avg_kindness!!) / 3
            binding.txtReviewScore.text = "${(grade * 10).roundToInt() / 10.0f}"
            binding.txtReviewCnt.text = "(${decimalFormatter.format(hospitalInfo.review_count)})"
            binding.txtHospitalName.text = hospitalInfo.name
            try {
                var distance = CommonUtils.calculateDistance(
                    context,
                    hospitalInfo.addr_lat!!.toDouble(),
                    hospitalInfo.addr_long!!.toDouble()
                )
                if (Math.round(distance.toDouble()).toString().length >= 4) {
                    distance =
                        (Math.round((distance.toDouble() / 1000) * 100) / 100.0).toString() + "km"
                } else {
                    distance = Math.round(distance.toDouble()).toString() + "m"
                }
                binding.txtHospitalInfo.text = "$distance, ${hospitalInfo.address}"
            } catch (e: Exception) {
                binding.txtHospitalInfo.text = "${hospitalInfo.address}"
            }

            if (hospitalInfo.premium_chk == true) {
                binding.txtPriceRank.text = "${context.getString(R.string.membership_rank_price01)}"
                binding.txtPrice.text =
                    "${decimalFormatter.format(hospitalInfo.premium_price!!.toInt())}"
            } else {
                binding.txtPriceRank.text = "${context.getString(R.string.membership_rank_price00)}"
                binding.txtPrice.text =
                    "${decimalFormatter.format(hospitalInfo.basic_price!!.toInt())}"
            }

            if (hospitalInfo.checkup != null) {
                binding.layoutFlow.removeAllViews()

                for (s in hospitalInfo.checkup!!) {
                    val type = CommonUtils.transLanguageHospitalType(s.type, context)

                    val tv = TextView(context)
                    tv.text = type
                    tv.setTextColor(context.getColor(R.color.font_title))
                    tv.setBackgroundResource(R.drawable.bg_10dp_rounding_point_sub)
                    tv.textSize = 12f
                    tv.includeFontPadding = false
                    tv.setPadding(20, 10, 20, 10)
                    val params = FlowLayout.LayoutParams(10, 10)
                    tv.layoutParams = params
                    tv.layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                    binding.layoutFlow.addView(tv)
                }
            }
        }
    }


}

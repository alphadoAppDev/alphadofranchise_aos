package kr.co.alphadopetshop.ui.health_check

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.tabs.TabLayout
import kr.co.alphadopetshop.BaseActivity
import kr.co.alphadopetshop.ImageLoadThread
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.*
import kr.co.alphadopetshop.model.AdvantageItem
import kr.co.alphadopetshop.model.ReviewUpdateModel
import kr.co.alphadopetshop.HospitalViewModel
import kr.co.alphadopetshop.ui.health_check.detail.*
import kr.co.alphadopetshop.ui.health_check.review.MakeReviewActivity
import kr.co.alphadopetshop.util.CharacterWrapTextView
import kr.co.alphadopetshop.util.CommonUtils
import kr.co.alphadopetshop.util.Constants
import kr.co.domain.model.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class HospitalDetailActivity : BaseActivity(), View.OnClickListener,
    ImageViewPagerAdapter.OnHospitalImageClickListener, MapView.POIItemEventListener,
    MapView.MapViewEventListener, OnSubjectClickListener {
    private var binding : ActivityHospitalDetailBinding? = null
    private var imagePagerAdapter : ImageViewPagerAdapter? = null
    private lateinit var hospitalDetail : HospitalDetail
    private lateinit var advantageAdapter : AdvantageAdapter
    private lateinit var hospitalViewModel : HospitalViewModel
    private var hospitalId : Int? = null
    private var position : Int? = null
    private var isReviewVisible = false
    private var reviewSort = Constants.SORT_TYPE_SCORE


    private var medicalSubjectAdapter: MedicalSubjectAdapter? = null
    private var medicalEquipAdapter : MedicalEquipAdapter? = null

    private var reviewAdapter: ReviewAdapter? = null
    private var pagingId : Int = 1
    private var isLastItem = false
    private var isCanScrollNestedScrollView = false
    private var isReviewLiveDataInitialFlag = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hospital_detail)

        Handler(Looper.getMainLooper()).postDelayed({
            init()
            hospitalViewModel.requestGetHospitalDetailInfo(hospitalId!!)
            observeHospitalDetailLiveData()
            observeProgressBar()

            binding!!.stickyScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                binding!!.topView.alpha = getAlphaForFloatingButton(scrollY, true)
                binding!!.btnBack.alpha = getAlphaForFloatingButton(scrollY, false)
            }
            setReviewRecyclerView()
            observeReviewLiveData()
            observeAddReviewLike()
            hospitalViewModel.requestGetHospitalReviews(hospital_id = hospitalId!!, page = pagingId, sort = reviewSort)

            binding!!.stickyScrollView.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

                    if (!isLastItem && isReviewVisible && !isCanScrollNestedScrollView) {
                        val value = v.getChildAt(0).measuredHeight - v.measuredHeight
                        if (scrollY >= value) {
                            isLastItem = true
                            hospitalViewModel.requestGetHospitalReviews(hospital_id = hospitalId!!, page = ++pagingId, sort = reviewSort)
                        }
                    }
                })
        },300)

    }
    private fun getAlphaForFloatingButton(scrollY : Int, isIncrease: Boolean) : Float {
        val minDist = 0
        val maxDist = 30000
        val maxDist2 = 1000
        when {
            scrollY > maxDist/100 -> {
                return if(isIncrease) { 1.0f }
                else {
                    0f
                }
            }
            scrollY < minDist -> {
                return if(isIncrease) {
                    0f
                } else {
                    1.0f
                }
            }
            else -> {
                return if(isIncrease) {
                    ((255.0/maxDist)* scrollY).toFloat()
                } else {
                    2.55f - ((255.0/maxDist2)* scrollY).toFloat()
                }
            }
        }
    }


    private fun init(){
        binding!!.activity = this
        hospitalId = intent.getIntExtra("hospital_id", 0)
        position = intent.getIntExtra("position",0)
        Log.d("test","hospitalId : $hospitalId")
        hospitalViewModel = ViewModelProvider(this, viewModelFactory).get(HospitalViewModel::class.java)
        binding!!.imgViewPager2.offscreenPageLimit = 3

        binding!!.btnBottom.setOnClickListener(this)
        binding!!.imgViewPager2.setOnClickListener(this)
        binding!!.btnTopBack.setOnClickListener(this)
        binding!!.btnBack.setOnClickListener(this)
        binding!!.topView.alpha = 0f

        binding!!.stickyScrollView.isHeaderTwo = true
        isReviewVisible = false

        binding!!.imgMembership.setOnClickListener(this)


        pagingId = 1
        isLastItem = false
        isCanScrollNestedScrollView = false
        isReviewLiveDataInitialFlag = false
        reviewSort = Constants.SORT_TYPE_SCORE
        binding!!.txtFilter00.setOnClickListener(this)
        binding!!.txtFilter01.setOnClickListener(this)

    }


    private fun observeHospitalDetailLiveData(){
        hospitalViewModel.hospitalDetailInfoLiveData.observe(this, {
            if(it != null) {
                if(it is Detail) {
                    Toast.makeText(applicationContext, it.msg, Toast.LENGTH_SHORT).show()
                } else {
                    hospitalDetail = it as HospitalDetail
                    val newImageList = ArrayList<HospitalImage>()
                    hospitalDetail.hospitalImages?.forEach {
                        if(it.url?.isNotEmpty() == true) newImageList.add(it)
                    }

                    hospitalDetail.hospitalImages = newImageList
                    setData()
                    setTabLayout()
                    setAdvantageRecycler()
                    setImageViewPager()
                    setImageType()

                    binding!!.txtBasicStandard00.text = hospitalDetail.hospitalInfo?.basic_standard_unit
                    binding!!.txtPremiumStandard00.text = hospitalDetail.hospitalInfo?.premium_standard_unit
                    setMap(hospitalDetail.hospitalInfo?.addr_lat!!.toDouble(), hospitalDetail.hospitalInfo?.addr_long!!.toDouble())

                    setWorkingHour()
                    setLocationInfo()
                    setMembershipInfo()
                    setMedicalSubjects()
                    setMedicalEquipment()
                    binding!!.visibleLayout.visibility = View.GONE

                    setReviewData()

                    binding!!.stickyScrollView.run {
                        header = binding!!.topView
                        secondHeader = binding!!.layoutTab
                        stickListener = { _ ->}
                        freeListener = { _ -> }
                        secondStickListener = { _ ->
                            binding!!.tabLayout.setBackgroundResource(R.drawable.bg_rect_bottom_shadow)
                        }
                        secondFreeListener = { _ ->
                            binding!!.tabLayout.setBackgroundResource(R.drawable.bg_rect_bottom_white)
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun observeProgressBar(){
        hospitalViewModel.mIsLoading.observe(this, {
            if(it) {
                binding!!.progressBar.visibility = View.VISIBLE
            } else {
                binding!!.progressBar.visibility = View.GONE
            }
        })
    }

    private fun setAdvantageRecycler(){
        advantageAdapter = AdvantageAdapter()
        advantageAdapter.addItem("title")
        advantageAdapter.addItem(AdvantageItem(order = 1, advantage = hospitalDetail.hospitalInfo?.introduce1 ?:""))
        advantageAdapter.addItem(AdvantageItem(order = 2, advantage = hospitalDetail.hospitalInfo?.introduce2 ?:""))
        advantageAdapter.addItem(AdvantageItem(order = 3, advantage = hospitalDetail.hospitalInfo?.introduce3 ?:""))
        binding!!.advantageRecycler.adapter = advantageAdapter
    }
    
    private fun setImageViewPager(){
        if(hospitalDetail.hospitalImages != null) {
            imagePagerAdapter = ImageViewPagerAdapter(this)
            imagePagerAdapter!!.submitList(hospitalDetail.hospitalImages!!.toList())
            binding!!.imgViewPager2.adapter = imagePagerAdapter
            binding!!.txtImgCount.text = "1/${hospitalDetail.hospitalImages!!.size}"

            binding!!.imgViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    binding!!.txtImgCount.text = "${position + 1}/${hospitalDetail.hospitalImages!!.size}"
                }
            })
        }
    }

    private fun setImageType(){
        if(hospitalDetail.hospitalImages != null) {
            val imageTypeList = ArrayList<String>()
            for(element in hospitalDetail.hospitalImages!!) {
                if(element.type!!.contains("main_img")) continue
                val parseType = CommonUtils.parseTypeToKorean(element.type!!)

                if(!imageTypeList.contains(parseType)) { imageTypeList.add(parseType) }
            }

            for(element in imageTypeList) {
                val tv = AppCompatTextView(applicationContext)
                tv.apply {
                    text = element
                    setTextColor(getColor(R.color.white))
                    setBackgroundResource(R.color.transparent_black)
                    textSize = 12f
                    setPadding(resources.getDimension(R.dimen.padding_5dp).toInt(), resources.getDimension(R.dimen.padding_4dp).toInt(), resources.getDimension(R.dimen.padding_5dp).toInt(), resources.getDimension(R.dimen.padding_4dp).toInt())
                    width = resources.getDimension(R.dimen.padding_55dp).toInt()
                    gravity = Gravity.CENTER
                    val param = LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT)
                    param.topMargin = resources.getDimension(R.dimen.padding_5dp).toInt()
                    layoutParams = param
                }
                binding!!.layoutImageType.addView(tv)
            }
        }
    }


    private fun setData(){
        if (hospitalDetail.hospitalInfo?.basic_chk == true
            && hospitalDetail.hospitalInfo?.premium_chk == true) {
            binding!!.txtMemberShip.text =
                "${getString(R.string.membership_rank00)}, ${getString(R.string.membership_rank01)}"
        } else {
            if (hospitalDetail.hospitalInfo?.basic_chk == true) {
                binding!!.txtMemberShip.text = "${getString(R.string.membership_rank00)}"
            } else {
                binding!!.txtMemberShip.text = "${getString(R.string.membership_rank01)}"
            }
        }

        binding!!.txtHospitalName.text = hospitalDetail.hospitalInfo?.name
        binding!!.txtTitleHospital.text = hospitalDetail.hospitalInfo?.name
        binding!!.txtAddress.text = hospitalDetail.hospitalInfo?.address
        binding!!.txtAddessHospital.text = hospitalDetail.hospitalInfo?.address
    }




    private fun setTabLayout(){
        binding!!.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) { }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> {
                        isReviewVisible = false
                        binding!!.detailLayout.visibility = View.VISIBLE
                        binding!!.reviewLayout.visibility = View.GONE
                        binding!!.btnBottom.text = "전화하기"
                        Handler(Looper.getMainLooper()).postDelayed({
                            binding!!.stickyScrollView.smoothScrollTo(0, binding!!.divider01.top - 80)
                        }, 200)

                    }
                    else -> {
                        isReviewVisible = true
                        binding!!.detailLayout.visibility = View.GONE
                        binding!!.reviewLayout.visibility = View.VISIBLE
                        binding!!.btnBottom.text = "리뷰 작성하기"
                        Handler(Looper.getMainLooper()).postDelayed({
                            binding!!.stickyScrollView.smoothScrollTo(0, binding!!.divider01.top - 80)
                        }, 200)
                    }
                }
            }
        })
    }



    override fun onClick(v: View?) {
        if(v?.id == R.id.btnBottom) {
            if(binding!!.tabLayout.selectedTabPosition == 0) {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${hospitalDetail.hospitalInfo?.tel}")))
            } else {
                val intent = Intent(this, MakeReviewActivity::class.java)
                intent.putExtra("hospital_id",hospitalId)
                reviewLauncher.launch(intent)
            }
        } else if(v?.id == R.id.imgViewPager2) {
            val bundle = Bundle()
            bundle.putSerializable("hospital_image", hospitalDetail.hospitalImages)

            val intent = Intent(this, HospitalImageActivity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)

        } else if(v?.id == R.id.btnBack || v?.id == R.id.btnTopBack){
            Handler(Looper.getMainLooper()).postDelayed({
                removeMapView()
                val intent = Intent()
                intent.putExtra("review_count", hospitalDetail.hospitalInfo?.review_count)
                intent.putExtra("position",position)
                setResult(Constants.RESULT_CODE_HOSPITAL_DETAIL, intent)
                finish()
            },300)


        } else if(v?.id == R.id.imgMembership) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://alphadopet.com/"))
            startActivity(intent)
        } else if(v?.id == R.id.txtFilter00) {
            pagingId = 1
            reviewSort = Constants.SORT_TYPE_SCORE
            binding!!.txtFilter00.setTextColor(applicationContext.getColor(R.color.font_title))
            binding!!.txtFilter00.setTypeface(binding!!.txtFilter00.typeface, Typeface.BOLD)
            binding!!.txtFilter01.setTextColor(applicationContext.getColor(R.color.font_sub))
            binding!!.txtFilter01.typeface = Typeface.DEFAULT

            isCanScrollNestedScrollView = true
            hospitalViewModel.initReviewLiveData()
//            reviewAdapter!!.submitList(null)
            hospitalViewModel.requestGetHospitalReviews(hospital_id = hospitalId!!, page = pagingId, sort = reviewSort)

        } else if(v?.id == R.id.txtFilter01) {
            pagingId = 1
            reviewSort = Constants.SORT_TYPE_CREATED_AT
            binding!!.txtFilter00.setTextColor(applicationContext.getColor(R.color.font_sub))
            binding!!.txtFilter00.typeface = Typeface.DEFAULT
            binding!!.txtFilter01.setTextColor(applicationContext.getColor(R.color.font_title))
            binding!!.txtFilter01.setTypeface(binding!!.txtFilter00.typeface, Typeface.BOLD)

            isCanScrollNestedScrollView = true
            hospitalViewModel.initReviewLiveData()
//            reviewAdapter!!.submitList(null)
            hospitalViewModel.requestGetHospitalReviews(hospital_id = hospitalId!!, page = pagingId, sort = reviewSort)
        }
    }

    private val reviewLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            binding!!.stickyScrollView.smoothScrollTo(0, binding!!.topLayout.bottom)
            if(it.data != null) {
                pagingId = 1
                isCanScrollNestedScrollView = true
                hospitalViewModel.initReviewLiveData()
//                reviewAdapter!!.submitList(null)
                hospitalViewModel.requestGetHospitalReviews(hospital_id = hospitalId!!, page = pagingId, sort = reviewSort)

                val totalReview = hospitalDetail.hospitalInfo!!.review_count!! + 1
                binding!!.txtTotalCnt.text = totalReview.toString()
                hospitalDetail.hospitalInfo!!.review_count = totalReview
            }
        }
    }

    private fun addReview(hospitalReviewItem: HospitalReviewItem){
        val currentList = reviewAdapter!!.currentList.toMutableList()
        currentList.add(hospitalReviewItem)
        reviewAdapter!!.submitList(currentList.toList())
    }

    private fun setWorkingHour(){
        try{
            binding!!.txtLunchTime.text =
                "점심시간 ${CommonUtils.parseHospitalInfoTime(hospitalDetail?.hospitalInfo?.lunch_start_time)}~${CommonUtils.parseHospitalInfoTime(hospitalDetail?.hospitalInfo?.lunch_end_time)}"
        }catch (e : Exception){
            binding!!.txtLunchTime.text =
                "점심시간 ${hospitalDetail?.hospitalInfo?.lunch_start_time}~${hospitalDetail?.hospitalInfo?.lunch_end_time}"
        }

        if(hospitalDetail.workingTimes != null) {
            for(element in hospitalDetail.workingTimes!!) {
                var time : String? = null
                if(workHourValidCheck(element.open_time, element.close_time)) {
                    time = "${CommonUtils.parseHospitalInfoTime(element.open_time)}~${CommonUtils.parseHospitalInfoTime(element.close_time)}"
                } else {
                    time = "휴무일"
                }

                when(element.day_of_week){
                    0 -> {
                        if(!workHourValidCheck(element.open_time, element.close_time))
                            binding!!.txtDayOfWeek00.setTextColor(applicationContext.getColor(R.color.point_red))
                        else
                            binding!!.txtDayOfWeek00.setTextColor(applicationContext.getColor(R.color.font_title))
                        binding!!.txtDayOfWeekTime00.text = time
                    }
                    1 -> {
                        if(!workHourValidCheck(element.open_time, element.close_time))
                            binding!!.txtDayOfWeek01.setTextColor(applicationContext.getColor(R.color.point_red))
                        else
                            binding!!.txtDayOfWeek01.setTextColor(applicationContext.getColor(R.color.font_title))
                        binding!!.txtDayOfWeekTime01.text = time
                    }
                    2 -> {
                        if(!workHourValidCheck(element.open_time, element.close_time))
                            binding!!.txtDayOfWeek02.setTextColor(applicationContext.getColor(R.color.point_red))
                        else
                            binding!!.txtDayOfWeek02.setTextColor(applicationContext.getColor(R.color.font_title))
                        binding!!.txtDayOfWeekTime02.text = time
                    }
                    3 -> {
                        if(!workHourValidCheck(element.open_time, element.close_time))
                            binding!!.txtDayOfWeek03.setTextColor(applicationContext.getColor(R.color.point_red))
                        else
                            binding!!.txtDayOfWeek03.setTextColor(applicationContext.getColor(R.color.font_title))
                        binding!!.txtDayOfWeekTime03.text = time
                    }
                    4 -> {
                        if(!workHourValidCheck(element.open_time, element.close_time))
                            binding!!.txtDayOfWeek04.setTextColor(applicationContext.getColor(R.color.point_red))
                        else
                            binding!!.txtDayOfWeek04.setTextColor(applicationContext.getColor(R.color.font_title))
                        binding!!.txtDayOfWeekTime04.text = time
                    }
                    5 -> {
                        if(!workHourValidCheck(element.open_time, element.close_time))
                            binding!!.txtDayOfWeek05.setTextColor(applicationContext.getColor(R.color.point_red))
                        else
                            binding!!.txtDayOfWeek05.setTextColor(applicationContext.getColor(R.color.font_title))
                        binding!!.txtDayOfWeekTime05.text = time
                    }
                    6 -> {
                        if(!workHourValidCheck(element.open_time, element.close_time))
                            binding!!.txtDayOfWeek06.setTextColor(applicationContext.getColor(R.color.point_red))
                        else
                            binding!!.txtDayOfWeek06.setTextColor(applicationContext.getColor(R.color.font_title))
                        binding!!.txtDayOfWeekTime06.text = time
                    }
                }
            }

            if(workHourValidCheck(hospitalDetail.hospitalInfo?.holiday_open_time, hospitalDetail.hospitalInfo?.holiday_close_time)) {
                binding!!.txtDayOfWeek07.setTextColor(applicationContext.getColor(R.color.font_title))
                binding!!.txtDayOfWeekTime07.text=
                    "${CommonUtils.parseHospitalInfoTime(hospitalDetail?.hospitalInfo?.holiday_open_time)}~${CommonUtils.parseHospitalInfoTime(hospitalDetail?.hospitalInfo?.holiday_close_time)}"
            } else {
                binding!!.txtDayOfWeek07.setTextColor(applicationContext.getColor(R.color.point_red))
                binding!!.txtDayOfWeekTime07.text = "휴무일"
            }
        }
    }

    private fun workHourValidCheck(openTime : String?, closeTime : String?) : Boolean {
        if(CommonUtils.parseHospitalInfoTime(openTime) == "00:00" && CommonUtils.parseHospitalInfoTime(closeTime) == "00:00") {
            //false이면 휴무일
            return false
        }

        return true
    }

    private fun setLocationInfo(){
        if(hospitalDetail.hospitalInfo?.enable_parking == true)
            binding!!.txtPark.text = "주차 가능"
        else
            binding!!.txtPark.text = "주차 불가능"

        binding!!.txtAddress.text = hospitalDetail.hospitalInfo?.address
        val directionsArray = hospitalDetail.hospitalInfo?.directions?.split("&")

        var subwayArray : List<String>? = null
        var subwayDescription = ""
        var bus = ""
        var busDescription = ""
        if(directionsArray != null) {
            try{
                subwayArray  = directionsArray[0].split("^")
                subwayDescription = directionsArray[1]
                bus = directionsArray[2]
                busDescription = directionsArray[3]
            }catch (e: Exception) {
                e.printStackTrace()
            }

            try{
                subwayArray?.forEachIndexed { i, txt ->
                    Log.d("test","txt : $txt")
                    val imageView = ImageView(applicationContext).apply {
                        if(getSubwayLineImage(txt) == null) {
                            setImageResource(R.drawable.hospital_subway_line_19)
                            this.setVisibility(View.INVISIBLE)
                        } else {
                            setImageResource(getSubwayLineImage(txt)!!)
                        }
                        scaleType = ImageView.ScaleType.FIT_XY
                        val params = LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT)
                        if(i > 0){
                            params.leftMargin = resources.getDimension(R.dimen.margin_2dp).toInt()
                        }
                        params.width = resources.getDimension(R.dimen.padding_20dp).toInt()
                        params.height = resources.getDimension(R.dimen.padding_20dp).toInt()
                        layoutParams = params
                    }
                    binding!!.layoutSubway.addView(imageView)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }

            binding!!.txtLocation.text = subwayDescription
            if(bus.trim().isNotEmpty())
                binding!!.txtBus.text = "${bus}\n${busDescription}"
            else
                binding!!.txtBus.text = "${busDescription}"
        }
    }

    private fun getSubwayLineImage(subwayType : String) : Int? {
        when(subwayType) {
            "1" -> return R.drawable.hospital_subway_line_1
            "2" -> return R.drawable.hospital_subway_line_2
            "3" -> return R.drawable.hospital_subway_line_3
            "4" -> return R.drawable.hospital_subway_line_4
            "5" -> return R.drawable.hospital_subway_line_5
            "6" -> return R.drawable.hospital_subway_line_6
            "7" -> return R.drawable.hospital_subway_line_7
            "8" -> return R.drawable.hospital_subway_line_8
            "9" -> return R.drawable.hospital_subway_line_9
            "분당" -> return R.drawable.hospital_subway_line_10
            "인천" -> return R.drawable.hospital_subway_line_11
            "신분" -> return R.drawable.hospital_subway_line_12
            "경의" -> return R.drawable.hospital_subway_line_13
            "경춘" -> return R.drawable.hospital_subway_line_14
            "공항" -> return R.drawable.hospital_subway_line_15
            "의정" -> return R.drawable.hospital_subway_line_16
            "수인" -> return R.drawable.hospital_subway_line_17
            "에버" -> return R.drawable.hospital_subway_line_18
            "자기" -> return R.drawable.hospital_subway_line_19
            else -> return null
        }
    }

    private fun setMembershipInfo(){
        val decimalFormat = DecimalFormat("#,###")
        if(!hospitalDetail.hospitalInfo?.basic_chk!!) {
            binding!!.layoutBasic.visibility = View.GONE
        } else {
            binding!!.txtBasicPrice.text = decimalFormat.format(hospitalDetail.hospitalInfo?.basic_price)
            binding!!.txtBasicCareTime.text = hospitalDetail.hospitalInfo?.basic_care_time
        }

        if(!hospitalDetail.hospitalInfo?.premium_chk!!) {
            binding!!.layoutPremium.visibility = View.GONE
        } else {
            binding!!.txtPremiumPrice.text = decimalFormat.format(hospitalDetail.hospitalInfo?.premium_price)
            binding!!.txtPremiumCareTime.text = hospitalDetail.hospitalInfo?.premium_care_time
        }


        if(hospitalDetail.hospitalInfo?.option1?.trim()?.isNotEmpty() == true) {
            binding!!.layoutOption00.visibility = View.VISIBLE
            binding!!.txtOption00.text = hospitalDetail.hospitalInfo?.option1
            binding!!.txtOptionTitle.visibility = View.VISIBLE
        }

        if(hospitalDetail.hospitalInfo?.option2?.trim()?.isNotEmpty() == true) {
            binding!!.layoutOption01.visibility = View.VISIBLE
            binding!!.txtOption01.text = hospitalDetail.hospitalInfo?.option2
            binding!!.txtOptionTitle.visibility = View.VISIBLE
        }

        if(hospitalDetail.hospitalInfo?.option3?.trim()?.isNotEmpty() == true) {
            binding!!.layoutOption02.visibility = View.VISIBLE
            binding!!.txtOption02.text = hospitalDetail.hospitalInfo?.option3
            binding!!.txtOptionTitle.visibility = View.VISIBLE
        }

        if(hospitalDetail.hospitalInfo?.option4?.trim()?.isNotEmpty() == true) {
            binding!!.layoutOption03.visibility = View.VISIBLE
            binding!!.txtOption03.text = hospitalDetail.hospitalInfo?.option4
            binding!!.txtOptionTitle.visibility = View.VISIBLE
        }
    }

    private fun setMedicalSubjects() {
        medicalSubjectAdapter = MedicalSubjectAdapter(this)
        if(hospitalDetail.medicalSubjects != null)
            hospitalDetail.medicalSubjects!!.forEachIndexed { i, element ->
                if(element.title?.trim()?.isEmpty() == true) return@forEachIndexed
                if(i == hospitalDetail.medicalSubjects!!.size - 1) { element.isLastItem = true }
                medicalSubjectAdapter!!.addItem(hospitalDetail.medicalSubjects!![i])

            }
        binding!!.recyclerSubject.adapter = medicalSubjectAdapter
    }

    private fun setMedicalEquipment(){
        medicalEquipAdapter = MedicalEquipAdapter()
        medicalEquipAdapter!!.addItem("제목")
        if(hospitalDetail.medicalEquipments != null) {
            hospitalDetail.medicalEquipments!!.forEachIndexed { i, element ->
                if(element.title?.trim()?.isEmpty() == true) return@forEachIndexed
                if(i == hospitalDetail.medicalEquipments!!.size - 1) { element.isLastItem = true }
                medicalEquipAdapter!!.addItem(hospitalDetail.medicalEquipments!![i])

            }
        }

        binding!!.medicalEquipRecycler.adapter = medicalEquipAdapter
    }

    override fun onPause() {
        super.onPause()
        removeMapView()
    }

    override fun onResume() {
        super.onResume()
        reloadMap()
    }

    fun removeMapView(){
        binding!!.mapView.removeAllViews()
    }

    fun reloadMap(){
        if(binding != null) {
            if(binding!!.mapView.getChildAt(0) == null){
                try{
                    setMap(hospitalDetail.hospitalInfo?.addr_lat!!.toDouble(), hospitalDetail.hospitalInfo?.addr_long!!.toDouble())
                }catch (e : Exception) {
                    Log.d("test","onResume mapView error : ${e.message}")
                }
            }
        }
    }

    private fun setMap(latitude: Double, longitude: Double) {

        try{
            val mapView = MapView(this@HospitalDetailActivity)
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true)


            val marker = MapPOIItem()
            marker.apply {
                itemName = hospitalDetail.hospitalInfo?.name
                mapPoint = MapPoint.mapPointWithGeoCoord(hospitalDetail.hospitalInfo?.addr_lat!!.toDouble(), hospitalDetail.hospitalInfo?.addr_long!!.toDouble())
                markerType = MapPOIItem.MarkerType.CustomImage
                customImageResourceId = R.drawable.icon_location_purple_18
                selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                customSelectedImageResourceId = R.drawable.icon_location_purple_24
            }
            mapView.setZoomLevel(1, true)
            mapView.addPOIItem(marker)
            mapView.setPOIItemEventListener(this)
            mapView.setMapViewEventListener(this)


            val mapPOIItem = MapPOIItem()
            mapPOIItem.isDraggable = false

            mapView.addPOIItem(mapPOIItem)
//        mapView.setOnTouchListener { v, event -> true }

            binding!!.mapView.addView(mapView)
        }catch (e: Exception) {
            Log.d("test","setMap error : ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onImageClicked() {
        val bundle = Bundle()
        bundle.putSerializable("hospital_image", hospitalDetail.hospitalImages)

        val intent = Intent(this, HospitalImageActivity::class.java)
        intent.putExtra("bundle",bundle)
        startActivity(intent)
    }

    fun moveInspectionDetail(num : Int) {
        var intent : Intent? = null
        var hospitalCheckup : HospitalCheckup? = null
        when(num) {
            0 -> {
                hospitalCheckup = searchHospitalCheckup("body")
                intent = Intent(applicationContext, HealthCheckActivity::class.java)
            }
            1 -> {
                hospitalCheckup = searchHospitalCheckup("blood")
                intent = Intent(applicationContext, BloodInspectionActivity::class.java)
            }
            2 -> {
                hospitalCheckup = searchHospitalCheckup("xray")
                intent = Intent(applicationContext, RadiationInspectionActivity::class.java)
            }
            else -> {
                hospitalCheckup = searchHospitalCheckup("ultrasound")
                intent = Intent(applicationContext, UltrasonicWaveInspectionActivity::class.java)
            }
        }

        if(hospitalCheckup != null){
            val bundle = Bundle()
            bundle.putSerializable("hospital_checkup", hospitalCheckup)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }


    private fun searchHospitalCheckup(type : String) : HospitalCheckup? {
        if(hospitalDetail.checkupInfo != null) {
            for(element in hospitalDetail.checkupInfo!!) {
                if(type == element.type) {
                    return element
                }
            }
        } else {
            return null
        }
        return null
    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {

    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

    }

    override fun onMapViewInitialized(p0: MapView?) {

    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {

    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        Log.d("test","onMapViewSingleTapped")
        binding!!.mapView.removeAllViews()
        val intent  = Intent(applicationContext, DetailMapActivity::class.java)
        intent.putExtra("latitude", hospitalDetail.hospitalInfo?.addr_lat!!.toDouble())
        intent.putExtra("longitude", hospitalDetail.hospitalInfo?.addr_long!!.toDouble())
        intent.putExtra("hospitalName",hospitalDetail.hospitalInfo?.name)
        startActivity(intent)
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {

    }

    override fun onSubjectClicked(item: MedicalSubject) {
        val intent = Intent(applicationContext, MedicalSubjectActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("medical_subject", item)
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }

    private fun setReviewRecyclerView(){
        reviewAdapter = ReviewAdapter(DiffUtilItemCallback())
        binding!!.reviewRecyclerReview.adapter = reviewAdapter
//        binding!!.reviewRecyclerReview.onFlingListener = object : RecyclerView.OnFlingListener(){
//            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
//                if(Math.abs(velocityY) > Constants.SCROLL_MAX_SPEED) {
//                    val topVelocityY = (Math.min(abs(velocityY), Constants.SCROLL_MAX_SPEED) * Math.signum(
//                        velocityY.toDouble()
//                    )).toInt()
//                    binding!!.reviewRecyclerReview.fling(velocityX, topVelocityY)
//                    return true
//                }
//                return false
//            }
//        }
    }


    private fun observeReviewLiveData() {
        hospitalViewModel.hospitalReviewLiveData.observe(this, {

            if (it != null) {
                if (it is Detail) {
                    Toast.makeText(applicationContext, it.msg, Toast.LENGTH_SHORT).show()
                    isCanScrollNestedScrollView = false

                } else {
                    val reviewItemList = it as ArrayList<HospitalReviewItem>
                    if(reviewItemList.size == reviewAdapter!!.itemCount) {
                        isLastItem = true
                        isCanScrollNestedScrollView = false
                    } else {
                        isLastItem = false

                        if(isCanScrollNestedScrollView) {
                            reviewAdapter!!.submitList(null)
                        }
                        reviewAdapter!!.submitList(reviewItemList.toList()) {

                            Handler(Looper.getMainLooper()).postDelayed({
                                if(isCanScrollNestedScrollView) {
                                    binding!!.stickyScrollView.smoothScrollTo(0, binding!!.divider01.top - 80)
                                    isCanScrollNestedScrollView = false
                                }
                            }, 500)
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
                isCanScrollNestedScrollView = false
            }
        })
    }

    private fun observeAddReviewLike(){
        hospitalViewModel.addReviewLikeLiveData.observe(this, {
            if(it != null) {
                if (it is Detail) {
                    Toast.makeText(applicationContext, it.msg, Toast.LENGTH_SHORT).show()
                } else {
                    val reviewUpdateModel = it as ReviewUpdateModel
                    val items = reviewAdapter!!.currentList

                    val newList = mutableListOf<HospitalReviewItem>()
                    newList.addAll(items)
                    newList[reviewUpdateModel.position] = HospitalReviewItem(
                        ReviewContent(
                            hospital_id = items[reviewUpdateModel.position].reviewContent.hospital_id,
                            writer = items[reviewUpdateModel.position].reviewContent.writer,
                            description = items[reviewUpdateModel.position].reviewContent.description,
                            like_count = (reviewUpdateModel.data as HospitalAddReviewResponse).like_count,
                            created_at = items[reviewUpdateModel.position].reviewContent.created_at
                        ),
                        reviewImages = items[reviewUpdateModel.position].reviewImages,
                        reviewScores = items[reviewUpdateModel.position].reviewScores
                    )

                    reviewAdapter!!.submitList(newList.toList())
                }
            } else {
                Toast.makeText(applicationContext, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setReviewData() {
        val decimalFormatter = DecimalFormat("###,###")
        binding!!.txtTotalCnt.text =
            decimalFormatter.format(hospitalDetail.hospitalInfo?.review_count)


        val grade = (hospitalDetail.hospitalInfo?.score_avg_hospital!!.toFloat()
                + hospitalDetail.hospitalInfo?.score_avg_satisfaction!!.toFloat()
                + hospitalDetail.hospitalInfo?.score_avg_kindness!!.toFloat()) / 3.0f

        val totalScore = (grade * 10).roundToInt() / 10.0f
        binding!!.txtTotalScore.text = totalScore.toString()
        binding!!.ratingBar.rating = totalScore

        binding!!.progressBarExamination.progress =
            (hospitalDetail.hospitalInfo?.score_avg_satisfaction!! * 10).toInt()
        binding!!.txtExaminationScore.text =
            (Math.round(hospitalDetail.hospitalInfo?.score_avg_satisfaction!! * 10) / 10.0f).toString()


        binding!!.progressBarFacility.progress =
            (hospitalDetail.hospitalInfo?.score_avg_hospital!! * 10).toInt()
        binding!!.txtFacilityScore.text =
            (Math.round(hospitalDetail.hospitalInfo?.score_avg_hospital!! * 10) / 10.0f).toString()



        binding!!.progressKindness.progress =
            (hospitalDetail.hospitalInfo?.score_avg_kindness!! * 10).toInt()
        binding!!.txtKindnessScore.text =
            (Math.round(hospitalDetail.hospitalInfo?.score_avg_kindness!! * 10) / 10.0f).toString()
    }

    override fun onBackPressed() {

        Handler(Looper.getMainLooper()).postDelayed({
            removeMapView()
            val intent = Intent()
            intent.putExtra("review_count",hospitalDetail.hospitalInfo?.review_count)
            intent.putExtra("position",position)
            setResult(Constants.RESULT_CODE_HOSPITAL_DETAIL, intent)
            finish()
        },300)
    }
}

class ImageViewPagerAdapter(
    val listener : OnHospitalImageClickListener
) : ListAdapter<HospitalImage, ImageViewPagerAdapter.ImageViewPagerViewHolder>(
    object : DiffUtil.ItemCallback<HospitalImage>(){
        override fun areItemsTheSame(oldItem: HospitalImage, newItem: HospitalImage): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: HospitalImage, newItem: HospitalImage): Boolean {
            return oldItem == newItem
        }
    }
){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewPagerViewHolder {
        val binding = DataBindingUtil.inflate<ItemImageBinding>(
            LayoutInflater.from(parent.context), R.layout.item_image, parent, false
        )
        return ImageViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewPagerViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }


    class ImageViewPagerViewHolder(private val binding : ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        private val mHandler = object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)

                try{
                    val bundle = msg.data
                    val url : String? = bundle.getString("url")

                    url?.let {
                        Glide.with(binding.root.context)
                            .load(Uri.parse(url))
                            .placeholder(R.drawable.bg_rect_solid_gray)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .into(binding.imgFacility)
                    }
                }catch (e: Exception){}

            }
        }

        fun bind(item : HospitalImage, listener : OnHospitalImageClickListener){
            ImageLoadThread(mHandler, item.url).start()

            binding.root.setOnClickListener { listener.onImageClicked() }
        }
    }

    interface OnHospitalImageClickListener{
        fun onImageClicked()
    }
}

class AdvantageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val items = ArrayList<Any>()
    private val ADVANTAGE_TITLE = 0
    private val ADVANTAGE_CONTENT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == ADVANTAGE_CONTENT) {
            val binding = DataBindingUtil.inflate<ItemAdvantageBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_advantage,
                parent,
                false
            )
            return AdvantageViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<ItemAdvantageTitleBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_advantage_title,
                parent,
                false
            )
            return AdvantageTitleViewHolder(binding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(items[position] is AdvantageItem)
                (holder as AdvantageViewHolder).bind(items[position] as AdvantageItem)
    }

    fun addItem(item : Any){
        items.add(item)
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        if(items[position] is AdvantageItem) {
            return ADVANTAGE_CONTENT
        } else {
            return ADVANTAGE_TITLE
        }
    }

    class AdvantageViewHolder(private val binding : ItemAdvantageBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item : AdvantageItem){
            if(item.advantage.isEmpty()){
                binding.topView.visibility = View.GONE
            } else {
                binding.topView.visibility = View.VISIBLE
                binding.txtOrder.text = item.order.toString()
                binding.txtAdvantage.text = item.advantage
            }
        }
    }

    class AdvantageTitleViewHolder(private val binding : ItemAdvantageTitleBinding) :  RecyclerView.ViewHolder(binding.root){}
}



class MedicalSubjectAdapter(
    private val listener: OnSubjectClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = ArrayList<MedicalSubject>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemHealthCheckSubjectBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_health_check_subject,
            parent,
            false
        )
        return MedicalSubjectViewHolder(binding, listener, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MedicalSubjectViewHolder).bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: MedicalSubject) {
        items.add(item)
    }

    class MedicalSubjectViewHolder(
        private val binding: ItemHealthCheckSubjectBinding,
        private val listener: OnSubjectClickListener,
        private val adapter: MedicalSubjectAdapter
    ) : RecyclerView.ViewHolder(binding.root) {
        private val decimalFormatter = DecimalFormat("###,###");

        fun bind(item: MedicalSubject) {
            if(item.isLastItem) {
                binding.underLine.visibility = View.GONE
            } else {
                binding.underLine.visibility = View.VISIBLE
            }

            binding.topView.setOnClickListener { listener.onSubjectClicked(item) }
            binding.txtSubjectPrice.text = decimalFormatter.format(item.price)
            binding.txtSubjectName.text = item.title

        }

    }
}

interface OnSubjectClickListener {
    fun onSubjectClicked(item: MedicalSubject)
}

class MedicalEquipAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var items = ArrayList<Any>()
    private val TYPE_TITLE = 0
    private val TYPE_CONTENT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_TITLE){
            val binding = DataBindingUtil.inflate<ItemMedicalEquipTitleBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_medical_equip_title,
                parent, false
            )
            return MedicalEquipTitleViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<ItemMedicalEquipBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_medical_equip,
                parent, false
            )
            return MedicalEquipViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(items[position] is MedicalEquipment) {
            (holder as MedicalEquipViewHolder).bind(items[position] as MedicalEquipment)
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item : Any){
        items.add(item)
    }

    override fun getItemViewType(position: Int): Int {
        if(items[position] is MedicalEquipment) {
            return TYPE_CONTENT
        } else {
            return TYPE_TITLE
        }
    }

    protected class MedicalEquipViewHolder(private val binding : ItemMedicalEquipBinding): RecyclerView.ViewHolder(binding.root)  {
        fun bind(item : MedicalEquipment){
            if(item.isLastItem) {
                binding.view00.visibility = View.GONE
            } else {
                binding.view00.visibility = View.VISIBLE
            }

            binding.txtTitle.text = item.title
            binding.txtMore.setOnClickListener {
                val dialog = CommonUtils.makeAlertDialog(R.layout.dialog_facility, binding.root.context, 0.8)
                dialog.findViewById<AppCompatTextView>(R.id.txtTitle).text = item.title
                dialog.findViewById<CharacterWrapTextView>(R.id.txtContent).text = item.description
                dialog.findViewById<ConstraintLayout>(R.id.layoutConfirm).setOnClickListener { dialog.dismiss() }
                dialog.findViewById<AppCompatTextView>(R.id.txtConfirm).setOnClickListener { dialog.dismiss() }
            }
        }
    }

    private class MedicalEquipTitleViewHolder(private val binding : ItemMedicalEquipTitleBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}

class DiffUtilItemCallback : DiffUtil.ItemCallback<HospitalReviewItem>(){

    override fun areItemsTheSame(
        oldItem: HospitalReviewItem,
        newItem: HospitalReviewItem
    ): Boolean {
        return if(oldItem.reviewScores.size > 0 && newItem.reviewScores.size > 0) {
            oldItem.reviewScores[0].review_id == newItem.reviewScores[0].review_id
        } else {
            oldItem.reviewContent.id == newItem.reviewContent.id
        }
    }

    override fun areContentsTheSame(
        oldItem: HospitalReviewItem,
        newItem: HospitalReviewItem
    ): Boolean {
        return oldItem.reviewContent.like_count == newItem.reviewContent.like_count
    }
}


class ReviewAdapter(
    private val reviewDiffUtilCallback: DiffUtilItemCallback
) : ListAdapter<HospitalReviewItem, ReviewAdapter.ReviewViewHolder>(reviewDiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = DataBindingUtil.inflate<ItemReviewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_review,
            parent,
            false
        )
        return ReviewViewHolder(binding, this)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<HospitalReviewItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    fun replaceItems(items: List<HospitalReviewItem>) {
        submitList(items)
    }


    class ReviewViewHolder(
        private val binding: ItemReviewBinding,
        private val adapter : ReviewAdapter
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: HospitalReviewItem) {
            binding.txtId.text = item.reviewContent.writer
            try {
                binding.txtDate.text =
                    CommonUtils.parseDate00(item.reviewContent.created_at.replace("T", " "))
            } catch (e: Exception) {
                binding.txtDate.text = item.reviewContent.created_at
            }

            binding.txtContent.text = item.reviewContent.description

            for (element in item.reviewScores) {
                if (element.type.contains("satisfaction")) {
                    binding.txtExaminationScore.text = element.score.toString()
                } else if (element.type.contains("hospital")) {
                    binding.txtFacilityScore.text = element.score.toString()
                } else if (element.type.contains("kindness")) {
                    binding.txtKindnessScore.text = element.score.toString()
                }
            }

            val imageAdapter = ReviewImageAdapter(item.reviewImages)
            binding.recyclerImage.adapter = imageAdapter

        }
    }
}


class ReviewImageAdapter(
    val items: ArrayList<ReviewImages>
) : RecyclerView.Adapter<ReviewImageAdapter.ReviewImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
        val binding = DataBindingUtil.inflate<ItemReviewInnerImgBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_review_inner_img,
            parent,
            false
        )
        return ReviewImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewImageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size


    class ReviewImageViewHolder(
        private val binding: ItemReviewInnerImgBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val mHandler = object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)

                val bundle = msg.data
                val url : String? = bundle.getString("url")

                url?.let {
                    Glide.with(binding.root.context)
                        .load(Uri.parse(url))
                        .placeholder(R.drawable.bg_rect_solid_gray)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(binding.imgPhoto)
                }
            }
        }

        fun bind(item: ReviewImages) {
//            ImageLoadThread(mHandler, item.url).start()
            Glide.with(binding.root.context)
                .load(Uri.parse(item.url))
                .into(binding.imgPhoto)
        }
    }
}
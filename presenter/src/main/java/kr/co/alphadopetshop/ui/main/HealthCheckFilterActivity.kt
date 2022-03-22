package kr.co.alphadopetshop.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.coroutines.*
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.ActivityHealthCheckFilterBinding
import kr.co.alphadopetshop.model.FilterModel
import kr.co.alphadopetshop.util.CommonUtils
import kr.co.alphadopetshop.util.Constants
import kr.co.alphadopetshop.util.FlowLayout
import kr.co.data.db.LocalPreference

class HealthCheckFilterActivity : AppCompatActivity(), View.OnClickListener {
    private var binding: ActivityHealthCheckFilterBinding? = null
    private lateinit var regionList: Array<FilterModel>
    private lateinit var membershipList: Array<FilterModel>
    private lateinit var distanceList: Array<FilterModel>
    private lateinit var reviewScoreList: Array<FilterModel>
    private lateinit var reviewCountList: Array<FilterModel>
    private var addressFilterSelected : String? = null
    private var premiumChk : String? = null
    private var basicChk : String? = null
    private var reviewScoreFilterSelected : String? = null
    private var reviewCountFilterSelected : String? = null
    private var textViewId: Int = 0
    private val TYPE_ADDRESS = 0
    private val TYPE_MEMBERSHIP = 1
    private val TYPE_DISTANCE = 2
    private val TYPE_REVIEW_SCORE = 3
    private val TYPE_REVIEW_COUNT = 4
    private var latitude : Float? = null
    private var longitude : Float? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_health_check_filter)

        init()
        setFilterContent()

    }

    private fun init() {
        binding!!.activity = this
        textViewId = 0
        addressFilterSelected = null
        premiumChk = null
        basicChk = null
        reviewScoreFilterSelected = null
        reviewCountFilterSelected = null
        latitude = null
        longitude = null



        binding!!.btnBottom.setOnClickListener(this)
    }

    private fun setFilterContent() {
        regionList = arrayOf(
            FilterModel("${getString(R.string.health_check_region_00)}", true),
            FilterModel("${getString(R.string.health_check_region_01)}", false),
            FilterModel("${getString(R.string.health_check_region_02)}", false),
            FilterModel("${getString(R.string.health_check_region_03)}", false),
            FilterModel("${getString(R.string.health_check_region_04)}", false),
            FilterModel("${getString(R.string.health_check_region_05)}", false),
            FilterModel("${getString(R.string.health_check_region_06)}", false),
            FilterModel("${getString(R.string.health_check_region_07)}", false),
            FilterModel("${getString(R.string.health_check_region_08)}", false),
            FilterModel("${getString(R.string.health_check_region_09)}", false),
            FilterModel("${getString(R.string.health_check_region_10)}", false),
            FilterModel("${getString(R.string.health_check_region_11)}", false),
            FilterModel("${getString(R.string.health_check_region_12)}", false)
        )


        if(LocalPreference.getInstance().getFilter1(applicationContext).trim().isNotEmpty()) {
            regionList[0].isSelected = false
            regionList[LocalPreference.getInstance().getFilter1(applicationContext).toInt()].isSelected = true
            addressFilterSelected = regionList[LocalPreference.getInstance().getFilter1(applicationContext).toInt()].name
            if(addressFilterSelected == getString(R.string.health_check_region_00)) {
                addressFilterSelected = null
            }

        }
        setFlowLayout(TYPE_ADDRESS, regionList, binding!!.flowLayoutRegion)

        membershipList = arrayOf(
            FilterModel("${getString(R.string.health_check_membership_00)}", true),
            FilterModel("${getString(R.string.health_check_membership_01)}", false),
            FilterModel("${getString(R.string.health_check_membership_02)}", false)
        )

        if(LocalPreference.getInstance().getFilter2(applicationContext).trim().isNotEmpty()) {
            val id = LocalPreference.getInstance().getFilter2(applicationContext).toInt()
            membershipList[0].isSelected = false

            if(id == 13){
                membershipList[0].isSelected = true
                premiumChk = null
                basicChk = null
            } else if(id == 14){
                membershipList[1].isSelected = true
                premiumChk = "0"
                basicChk = "1"
            } else {
                membershipList[2].isSelected = true
                premiumChk = "1"
                basicChk = "0"
            }

        }
        setFlowLayout(TYPE_MEMBERSHIP, membershipList, binding!!.flowLayoutMemberShip)

        distanceList = arrayOf(FilterModel("${getString(R.string.health_check_distance00)}", false))
        if(LocalPreference.getInstance().getFilter3(applicationContext).trim().isNotEmpty()) {
            distanceList[0].isSelected = true
            val location = CommonUtils.getLatLng(applicationContext, getSystemService(Context.LOCATION_SERVICE) as LocationManager)
            latitude = location.latitude.toFloat()
            longitude = location.longitude.toFloat()
            reviewScoreFilterSelected = null
            reviewCountFilterSelected = null
        }
        setFlowLayout(TYPE_DISTANCE, distanceList, binding!!.flowLayoutDistance)

        reviewScoreList = arrayOf(
            FilterModel("${getString(R.string.health_check_review_score00)}", false),
            FilterModel("${getString(R.string.health_check_review_score01)}", false),
            FilterModel("${getString(R.string.health_check_review_score02)}", false),
            FilterModel("${getString(R.string.health_check_review_score03)}", false),
        )
        if(LocalPreference.getInstance().getFilter4(applicationContext).trim().isNotEmpty()) {
            val id = LocalPreference.getInstance().getFilter4(applicationContext).trim().toInt()
            reviewScoreList[0].isSelected = false
            latitude = null
            longitude = null
            reviewCountFilterSelected = null

            if(id == 17) {
                reviewScoreList[0].isSelected = true
                reviewScoreFilterSelected = "score|desc"
            } else if(id == 18) {
                reviewScoreList[1].isSelected = true
                reviewScoreFilterSelected = "score_avg_satisfaction|desc"
            } else if(id == 19) {
                reviewScoreList[2].isSelected = true
                reviewScoreFilterSelected = "score_avg_hospital|desc"
            }  else {
                reviewScoreList[3].isSelected = true
                reviewScoreFilterSelected = "score_avg_kindness|desc"
            }

        }
        setFlowLayout(TYPE_REVIEW_SCORE, reviewScoreList, binding!!.flowLayoutReviewScore)

        reviewCountList = arrayOf(
            FilterModel("${getString(R.string.health_check_review_count00)}", false),
            FilterModel("${getString(R.string.health_check_review_count01)}", false),
        )

        if(LocalPreference.getInstance().getFilter5(applicationContext).trim().isNotEmpty()) {
            val id = LocalPreference.getInstance().getFilter5(applicationContext).trim().toInt()
            latitude = null
            longitude = null
            reviewScoreFilterSelected = null

            if(id == 21){
                reviewCountList[0].isSelected = true
                reviewCountFilterSelected = "review_count|desc"
            } else {
                reviewCountList[1].isSelected = true
                reviewCountFilterSelected = "review_count|asc"
            }
        }

        setFlowLayout(TYPE_REVIEW_COUNT, reviewCountList, binding!!.flowLayoutCountScore)
    }

    private fun setFlowLayout(type: Int, array: Array<FilterModel>, flowLayout: FlowLayout) {
        when (type) {
            TYPE_ADDRESS -> textViewId = 0
            TYPE_MEMBERSHIP -> textViewId = 13
            TYPE_DISTANCE -> textViewId = 16
            TYPE_REVIEW_SCORE -> textViewId = 17
            TYPE_REVIEW_COUNT -> textViewId = 21
        }

        (array.indices).forEach { i ->
            val params = FlowLayout.LayoutParams(
                resources.getDimension(R.dimen.filter_horizontal).toInt(),
                resources.getDimension(R.dimen.filter_vertical).toInt()
            )
            val tv = makeTextView(
                array = array,
                params = params,
                num = i,
                array[i].isSelected
            )
            flowLayout.addView(tv)
        }
    }




    private fun makeTextView(
        array: Array<FilterModel>,
        params: FlowLayout.LayoutParams,
        num: Int,
        isSelected: Boolean
    ): AppCompatTextView {
        return AppCompatTextView(applicationContext).apply {
            tag = num
            text = if (array[num].name == getString(R.string.health_check_membership_00)) {
                "전체"
            } else {
                array[num].name
            }

            isClickable = true
            if (isSelected) {
                setTextColor(applicationContext.getColor(R.color.white))
                setBackgroundResource(R.drawable.bg_10dp_rounding_point)
            } else {
                setTextColor(applicationContext.getColor(R.color.font_title))
                setBackgroundResource(R.drawable.bg_10dp_rounding_stroke_grey)
            }

            textSize = 14f
            includeFontPadding = false
            val paddingSize = resources.getDimension(R.dimen.filter_padding)
            setPadding(
                paddingSize.toInt(),
                paddingSize.toInt(),
                paddingSize.toInt(),
                paddingSize.toInt()
            )
            layoutParams = params
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            id = textViewId++

            setOnClickListener(this@HealthCheckFilterActivity)
        }
    }

    private fun initVariable(type: Int) {
        when (type) {
            2 -> {
                reviewScoreList = arrayOf(
                    FilterModel("${getString(R.string.health_check_review_score00)}", false),
                    FilterModel("${getString(R.string.health_check_review_score01)}", false),
                    FilterModel("${getString(R.string.health_check_review_score02)}", false),
                    FilterModel("${getString(R.string.health_check_review_score03)}", false),
                )
                reviewCountList = arrayOf(
                    FilterModel("${getString(R.string.health_check_review_count00)}", false),
                    FilterModel("${getString(R.string.health_check_review_count01)}", false),
                )
            }
            3 -> {
                distanceList =
                    arrayOf(FilterModel("${getString(R.string.health_check_distance00)}", false))
                reviewCountList = arrayOf(
                    FilterModel("${getString(R.string.health_check_review_count00)}", false),
                    FilterModel("${getString(R.string.health_check_review_count01)}", false),
                )
            }
            4 -> {
                distanceList =
                    arrayOf(FilterModel("${getString(R.string.health_check_distance00)}", false))
                reviewScoreList = arrayOf(
                    FilterModel("${getString(R.string.health_check_review_score00)}", false),
                    FilterModel("${getString(R.string.health_check_review_score01)}", false),
                    FilterModel("${getString(R.string.health_check_review_score02)}", false),
                    FilterModel("${getString(R.string.health_check_review_score03)}", false),
                )
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBottom -> {
                val intent = Intent()
                intent.putExtra("address",addressFilterSelected)
                intent.putExtra("premium",premiumChk)
                intent.putExtra("basic",basicChk)
                intent.putExtra("latitude",latitude)
                intent.putExtra("longitude",longitude)
                intent.putExtra("review_score",reviewScoreFilterSelected)
                intent.putExtra("review_count",reviewCountFilterSelected)
                setResult(Constants.RESULT_CODE_FILTER, intent)
                finish()
            }
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 -> {
                LocalPreference.getInstance().setFilter1(v.id.toString(), applicationContext)
                binding!!.flowLayoutRegion.removeAllViews()

                for (i in regionList.indices) {
                    if (i == v.tag) {
                        regionList[v.tag as Int].isSelected = true
                        if(v.id == 0) {
                            addressFilterSelected = null
                        } else {
                            addressFilterSelected = regionList[v.tag as Int].name
                        }
                    } else {
                        regionList[i].isSelected = false
                    }
                }
                setFlowLayout(TYPE_ADDRESS, regionList, binding!!.flowLayoutRegion)
            }

            //멤버심 전체, 베이직, 프리미엄
            13, 14, 15 -> {
                LocalPreference.getInstance().setFilter2(v.id.toString(), applicationContext)
                binding!!.flowLayoutMemberShip.removeAllViews()

                for (i in membershipList.indices) {
                    if (i == v.tag) {
                        membershipList[v.tag as Int].isSelected = true

                        if(v.id == 13){
                            premiumChk = null
                            basicChk = null
                        } else if(v.id == 14){
                            premiumChk = "0"
                            basicChk = "1"
                        } else {
                            premiumChk = "1"
                            basicChk = "0"
                        }
                    } else {
                        membershipList[i].isSelected = false
                    }
                }
                setFlowLayout(TYPE_MEMBERSHIP, membershipList, binding!!.flowLayoutMemberShip)
            }

            16, 17, 18, 19, 20, 21, 22 -> {
                if(v.id == 16 ) {
                    LocalPreference.getInstance().setFilter3("${v.id}", applicationContext)
                } else if(v.id == 17 || v.id == 18 || v.id == 19 || v.id == 20){
                    LocalPreference.getInstance().setFilter4("${v.id}", applicationContext)
                } else {
                    LocalPreference.getInstance().setFilter5("${v.id}", applicationContext)
                }
                var array : Array<FilterModel>? = null
                if (v.id == 16) {
                    val listener = object : PermissionListener {
                        override fun onPermissionGranted() {
                            binding!!.flowLayoutDistance.removeAllViews()
                            binding!!.flowLayoutReviewScore.removeAllViews()
                            binding!!.flowLayoutCountScore.removeAllViews()
                            initVariable(2)
                            array = distanceList
                            setFilterOption(id = v.id, array = array!!, v = v)
                        }

                        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {}
                    }

                    TedPermission.with(this)
                        .setPermissionListener(listener)
                        .setDeniedMessage(getString(R.string.permisson_location_denied))
                        .setPermissions(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ).check()

                } else if (v.id == 17 || v.id == 18 || v.id == 19 || v.id == 20) {
                    LocalPreference.getInstance().setFilter4("${v.id}", applicationContext)
                    binding!!.flowLayoutDistance.removeAllViews()
                    binding!!.flowLayoutReviewScore.removeAllViews()
                    binding!!.flowLayoutCountScore.removeAllViews()
                    initVariable(3)
                    array = reviewScoreList


                } else {
                    binding!!.flowLayoutDistance.removeAllViews()
                    binding!!.flowLayoutReviewScore.removeAllViews()
                    binding!!.flowLayoutCountScore.removeAllViews()
                    initVariable(4)
                    array = reviewCountList
                }

                if(array != null) {
                    setFilterOption(id = v.id, array = array!!, v = v)
                }

            }
        }
    }

    private fun setFilterOption(id : Int, array : Array<FilterModel>, v : View){
        for (i in array.indices) {
            if (i == v.tag) {
                array[v.tag as Int].isSelected = !array[v.tag as Int].isSelected

                if(array[v.tag as Int].isSelected) {
                    if(id == 16) {
                        val location = CommonUtils.getLatLng(applicationContext, getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                        latitude = location.latitude.toFloat()
                        longitude = location.longitude.toFloat()
                        reviewScoreFilterSelected = null
                        reviewCountFilterSelected = null
                        setFilterToLocal(Constants.TYPE_DISTANCE, id)

                    } else if (id == 17 || id == 18 || id == 19 || id == 20) {
                        latitude = null
                        longitude = null
                        reviewCountFilterSelected = null

                        if(id == 17) {
                            reviewScoreFilterSelected = "score|desc"
                        } else if(id == 18) {
                            reviewScoreFilterSelected = "score_avg_satisfaction|desc"
                        } else if(id == 19) {
                            reviewScoreFilterSelected = "score_avg_hospital|desc"
                        }  else {
                            reviewScoreFilterSelected = "score_avg_kindness|desc"
                        }

                        setFilterToLocal(Constants.TYPE_SCORE, id)
                    } else {
                        latitude = null
                        longitude = null
                        reviewScoreFilterSelected = null

                        if(id == 21){
                            reviewCountFilterSelected = "review_count|desc"
                        } else {
                            reviewCountFilterSelected = "review_count|asc"
                        }
                        setFilterToLocal(Constants.TYPE_REVIEW, id)
                    }

                } else {
                    latitude = null
                    longitude = null
                    reviewScoreFilterSelected = null
                    reviewCountFilterSelected = null
                    Log.d("test","id : ${id}")

                    if(id == 16) {
                        LocalPreference.getInstance().setFilter3("", applicationContext)
                    } else if (id == 17 || id == 18 || id == 19 || id == 20) {
                        LocalPreference.getInstance().setFilter4("", applicationContext)
                    } else {
                        LocalPreference.getInstance().setFilter5("", applicationContext)
                    }
                }

            } else {
                array[i].isSelected = false
            }
        }
        setFlowLayout(TYPE_DISTANCE, distanceList, binding!!.flowLayoutDistance)
        setFlowLayout(TYPE_REVIEW_SCORE, reviewScoreList, binding!!.flowLayoutReviewScore)
        setFlowLayout(TYPE_REVIEW_COUNT, reviewCountList, binding!!.flowLayoutCountScore)
    }

    private fun setFilterToLocal(type : Int, id : Int){
        LocalPreference.getInstance().setFilter3("", applicationContext)
        LocalPreference.getInstance().setFilter4("", applicationContext)
        LocalPreference.getInstance().setFilter5("", applicationContext)

        if(type == Constants.TYPE_DISTANCE) {
            LocalPreference.getInstance().setFilter3(id.toString(), applicationContext)
        } else if(type == Constants.TYPE_SCORE) {
            LocalPreference.getInstance().setFilter4(id.toString(), applicationContext)
        } else {
            LocalPreference.getInstance().setFilter5(id.toString(), applicationContext)
        }
    }
}

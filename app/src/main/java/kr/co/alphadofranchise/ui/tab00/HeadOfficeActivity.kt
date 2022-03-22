package kr.co.alphadofranchise.ui.tab00

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import kr.co.alphadofranchise.R
import kr.co.alphadofranchise.base.BaseActivity
import kr.co.alphadofranchise.databinding.ActivityHeadOfficeBinding
import kr.co.alphadofranchise.models.HeadOfficeCell
import kr.co.alphadofranchise.models.HeadOfficeInnerCell
import kr.co.alphadofranchise.ui.StoreOutAdapter
import kr.co.alphadofranchise.viewmodel.FranchiseViewModel
import kr.co.domain.model.FranchiseRegion

class HeadOfficeActivity : BaseActivity() {
    private var binding : ActivityHeadOfficeBinding? = null
    private lateinit var franchiseViewModel : FranchiseViewModel
    private lateinit var headOfficeSalesAdapter : StoreOutAdapter //본사 업체 매출현황 recyclerview
    private lateinit var headOfficeRevenueAdapter : StoreOutAdapter //본사 업체 수익 현황 recyclerview
    private var storeCode: String? = null
    private var region : String? = null
    private var monthInfoList : ArrayList<FranchiseRegion>? = null
    private var titleArray = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeadOfficeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        init()
        if(monthInfoList != null){
            franchiseViewModel.requestRegionInfo2(code = storeCode!!)
            observeRegionInfoLiveData()
        } else {
            setData(monthInfoList!!)
        }
    }

    private fun init(){
        storeCode = intent.getStringExtra("code")
        region = intent.getStringExtra("region")
        franchiseViewModel = ViewModelProvider(this, viewModelFactory).get(FranchiseViewModel::class.java)
        titleArray.add("")
        titleArray.add("헬스케어\n디바이스")
        titleArray.add("사료")
        titleArray.add("구독")
        titleArray.add("총")

        try{
            monthInfoList = intent.getSerializableExtra("monthList") as ArrayList<FranchiseRegion>
        }catch (e: Exception) { e.printStackTrace() }

        binding!!.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }
    }

    private fun observeRegionInfoLiveData(){
        franchiseViewModel.regionInfoLiveData.observe(this, {
            if(it != null) {
                if(it is String) {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                } else {
                    val monthInfoList = it as ArrayList<FranchiseRegion>
                    setData(monthInfoList)
                }
            } else {
                Toast.makeText(applicationContext,"인터넷 연결 끊김", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setData(monthInfoList : ArrayList<FranchiseRegion>){
        if(monthInfoList.size > 4) {
            binding!!.txtHeadOfficeDate.text = monthInfoList[4].date?:""
            binding!!.txtHeadOfficeTitle.text = "본사 매출 현황"
            monthInfoList[4].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.txt03.text = it2[0] }  }
            monthInfoList[4].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.txtHeadOfficeMenu00.text = titleArray[1] }  }
            monthInfoList[4].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.txtHeadOfficeMenu01.text = it2[2] }  }
            monthInfoList[4].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.txtHeadOfficeMenu02.text = it2[3] }  }
            monthInfoList[4].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.txtHeadOfficeMenu03.text = it2[4] }  }


            monthInfoList[4].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtSalesForYear.text = it2[0] } }
            monthInfoList[4].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtSalesForYearMenu00.text = it2[1] } }
            monthInfoList[4].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtSalesForYearMenu01.text = it2[2] } }
            monthInfoList[4].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtSalesForYearMenu02.text = it2[3] } }
            monthInfoList[4].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtSalesForYearMenu03.text = it2[4] } }
        }


        if(monthInfoList.size > 5) {
            binding!!.txtHeadOfficeMonthSales.text = monthInfoList[5].title
            setRecyclerView(5, monthInfoList[5])
        }
        if(monthInfoList.size > 6){
            binding!!.txtHeadOfficeMonthProfit.text = monthInfoList[6].title
            setRecyclerView(6, monthInfoList[6])
        }

        setTextColorPartial()
    }

    private fun setRecyclerView(type : Int, region : FranchiseRegion){
        val innerList = ArrayList<Any>()

        region.table!!.line03?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line04?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line05?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line06?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line07?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line08?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line09?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line10?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line11?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line12?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line13?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line14?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line15?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line16?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line17?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line18?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line19?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line20?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line21?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line22?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line23?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line24?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line25?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line26?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line27?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line28?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line29?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line30?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line31?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line32?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line33?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line34?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line35?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line36?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line37?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line38?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line39?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line40?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line41?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line42?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line43?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line44?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line45?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line46?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line47?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line48?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line49?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line50?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line51?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line52?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line53?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line54?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line55?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line56?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line57?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line58?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line59?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line60?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line61?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line62?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line63?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line64?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line65?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line66?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line67?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line68?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line69?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line70?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line71?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line72?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line73?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line74?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line75?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line76?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line77?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line78?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line79?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line80?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line81?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line82?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line83?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line84?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line85?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line86?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line87?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line88?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line89?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line90?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line91?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line92?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line93?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line94?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line95?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line96?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line97?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line98?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line99?.let { innerList.add(HeadOfficeInnerCell(it)) }
        region.table!!.line100?.let { innerList.add(HeadOfficeInnerCell(it)) }

        val outList = ArrayList<Any>()
        region.table!!.line01?.let { it1 ->
            region.table!!.line02?.let { it2 ->
                outList.add(HeadOfficeCell(it1, it2, innerList, false))
            }
        }

        if(type == 5) {
            headOfficeSalesAdapter  = StoreOutAdapter(outList)
            binding!!.headOfficeSalesRecyclerView.adapter = headOfficeSalesAdapter

        } else {
            headOfficeRevenueAdapter  = StoreOutAdapter(outList)
            binding!!.headOfficeRevenueRecyclerView.adapter = headOfficeRevenueAdapter
        }
    }

    private fun setTextColorPartial() {
        var sp = SpannableStringBuilder(binding!!.txtSalesForYear.text.toString())
        sp.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.font_sub)), 9, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        sp.setSpan(RelativeSizeSpan(0.7f), 9,14, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        binding!!.txtSalesForYear.text = sp

        sp = SpannableStringBuilder(binding!!.txtHeadOfficeMonthProfit.text.toString())
        sp.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.font_sub)), 5, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        sp.setSpan(RelativeSizeSpan(0.7f), 5,10, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        binding!!.txtHeadOfficeMonthProfit.text = sp
    }
    

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }

}
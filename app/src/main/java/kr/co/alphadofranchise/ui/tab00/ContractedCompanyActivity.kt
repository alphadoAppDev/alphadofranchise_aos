package kr.co.alphadofranchise.ui.tab00

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
import kr.co.alphadofranchise.databinding.ActivityContractedCompanyBinding
import kr.co.alphadofranchise.models.HeadOfficeCell
import kr.co.alphadofranchise.models.HeadOfficeInnerCell
import kr.co.alphadofranchise.models.RegionCell
import kr.co.alphadofranchise.models.RegionInnerCell
import kr.co.alphadofranchise.ui.StoreOutAdapter
import kr.co.alphadofranchise.viewmodel.FranchiseViewModel
import kr.co.domain.model.FranchiseRegion

class ContractedCompanyActivity : BaseActivity() {
    private var binding : ActivityContractedCompanyBinding? = null
    private lateinit var franchiseViewModel : FranchiseViewModel
    private lateinit var salesAdapter : StoreOutAdapter //협약 업체 매출현황 recyclerview
    private lateinit var revenueAdapter : StoreOutAdapter //협약 업체 수익 현황 recyclerview
    private lateinit var dividendsAdapter : StoreOutAdapter //본사 이익 배당 수익 recyclerview
    private var storeCode: String? = null
    private var region : String? = null
    private var monthInfoList : ArrayList<FranchiseRegion>? = null
    private var titleArray = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContractedCompanyBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        
        init()
        if(monthInfoList != null){
            franchiseViewModel.requestRegionInfo2(code = storeCode!!)
            observeRegionInfoLiveData()
        } else {
            binding!!.txtRegion.text = region
            setData(monthInfoList!!)
        }
    }
    
    private fun init(){
        storeCode = intent.getStringExtra("code")
        region = intent.getStringExtra("region")
        try{
            monthInfoList = intent.getSerializableExtra("monthList") as ArrayList<FranchiseRegion>
        }catch (e: Exception) { e.printStackTrace() }

        franchiseViewModel = ViewModelProvider(this, viewModelFactory).get(FranchiseViewModel::class.java)
        binding!!.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }

        titleArray.add("")
        titleArray.add("병원")
        titleArray.add("미용")
        titleArray.add("유치원")
        titleArray.add("카페")
        titleArray.add("장례")
        titleArray.add("총")

    }

    private fun observeRegionInfoLiveData(){
        franchiseViewModel.regionInfoLiveData.observe(this, {
            if(it != null) {
                if(it is String) {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                } else {
                    binding!!.txtRegion.text = region
                    val monthInfoList = it as ArrayList<FranchiseRegion>
                    setData(monthInfoList)
                }
            } else {
                Toast.makeText(applicationContext,"인터넷 연결 끊김", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setRecyclerView(type : Int, region : FranchiseRegion){
        val innerList = ArrayList<Any>()
        val outList = ArrayList<Any>()

        if(type == 1 || type == 2) {
            region.table!!.line03?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line04?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line05?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line06?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line07?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line08?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line09?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line10?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line11?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line12?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line13?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line14?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line15?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line16?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line17?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line18?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line19?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line20?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line21?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line22?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line23?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line24?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line25?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line26?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line27?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line28?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line29?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line30?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line31?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line32?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line33?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line34?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line35?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line36?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line37?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line38?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line39?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line40?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line41?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line42?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line43?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line44?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line45?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line46?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line47?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line48?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line49?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line50?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line51?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line52?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line53?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line54?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line55?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line56?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line57?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line58?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line59?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line60?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line61?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line62?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line63?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line64?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line65?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line66?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line67?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line68?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line69?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line70?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line71?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line72?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line73?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line74?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line75?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line76?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line77?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line78?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line79?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line80?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line81?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line82?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line83?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line84?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line85?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line86?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line87?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line88?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line89?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line90?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line91?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line92?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line93?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line94?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line95?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line96?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line97?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line98?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line99?.let { innerList.add(RegionInnerCell(it)) }
            region.table!!.line100?.let { innerList.add(RegionInnerCell(it)) }






            region.table!!.line01?.let { it1 ->
                region.table!!.line02?.let{ it2 ->
                    outList.add(RegionCell(it1, it2, innerList, false))
                }
            }
        } else {
            region.table!!.line03?.let { innerList.add(HeadOfficeInnerCell(it)) }
            region.table!!.line04?.let { innerList.add(HeadOfficeInnerCell(it)) }
            region.table!!.line05?.let { innerList.add(HeadOfficeInnerCell(it)) }
            region.table!!.line06?.let { innerList.add(HeadOfficeInnerCell(it)) }
            region.table!!.line07?.let { innerList.add(HeadOfficeInnerCell(it)) }
            region.table!!.line08?.let { innerList.add(HeadOfficeInnerCell(it)) }
            region.table!!.line09?.let { innerList.add(HeadOfficeInnerCell(it)) }
            region.table!!.line10?.let { innerList.add(HeadOfficeInnerCell(it)) }

            region.table!!.line01?.let { it1 ->
                region.table!!.line02?.let { it2 ->
                    outList.add(HeadOfficeCell(it1, it2, innerList, false))
                }
            }
        }


        if(type == 1) {
            if(outList.size > 0) {
                salesAdapter  = StoreOutAdapter(outList)
                binding!!.salesRecyclerView.adapter = salesAdapter
            }
        } else if(type == 2) {
            if(outList.size > 0) {
                revenueAdapter  = StoreOutAdapter(outList)
                binding!!.revenueRecyclerView.adapter = revenueAdapter
            }
        } else {
            if(outList.size > 0) {
                dividendsAdapter = StoreOutAdapter(outList)
                binding!!.dividendRecyclerView.adapter = dividendsAdapter
            }
        }
    }

    private fun setTextColorPartial() {
        var sp = SpannableStringBuilder(binding!!.txtSales.text.toString())
        sp.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.font_sub)), 9, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        sp.setSpan(RelativeSizeSpan(0.7f), 9,14, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        binding!!.txtSales.text = sp


        sp = SpannableStringBuilder(binding!!.txtRegionMonthProfitTitle.text.toString())
        sp.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.font_sub)), 9, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        sp.setSpan(RelativeSizeSpan(0.7f), 9,13, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        binding!!.txtRegionMonthProfitTitle.text = sp

        sp = SpannableStringBuilder(binding!!.txtDividendTitle.text.toString())
        sp.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.font_sub)), 12, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        sp.setSpan(RelativeSizeSpan(0.58f), 12,16, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        binding!!.txtDividendTitle.text = sp
    }

    private fun setData(monthInfoList : ArrayList<FranchiseRegion>){
        if(monthInfoList.size > 0) {
            binding!!.txtRegionDate.text = monthInfoList[0].date?:""
            binding!!.txtRegionTitle.text = monthInfoList[0].title?:""
            monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.txt00.text = it2[0] }  }
            monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.storeMenu00.text = it2[1] }  }
            monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.storeMenu01.text = it2[2] }  }
            monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.storeMenu02.text = it2[3] }  }
            monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.storeMenu03.text = it2[4] }  }
            monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.storeMenu04.text = it2[5] }  }
            monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.storeMenu05.text = it2[6] }  }


            monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount.text = it2[0] } }
            monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount00.text = it2[1] } }
            monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount01.text = it2[2] } }
            monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount02.text = it2[3] } }
            monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount03.text = it2[4] } }
            monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount04.text = it2[5] } }
            monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount05.text = it2[6] } }
        }


        if(monthInfoList.size > 0) {
            monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales.text = it2[0] } }
            monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales00.text = it2[1] } }
            monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales01.text = it2[2] } }
            monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales02.text = it2[3] } }
            monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales03.text = it2[4] } }
            monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales04.text = it2[5] } }
            monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales05.text = it2[6] } }
        }


        if(monthInfoList.size > 1)  {
            binding!!.txtRegionMonthSalesTitle.text = monthInfoList[1].title
            setRecyclerView(1, monthInfoList[1])
        }
        if(monthInfoList.size > 2) {
            binding!!.txtRegionMonthProfitTitle.text = monthInfoList[2].title
            setRecyclerView(2, monthInfoList[2])
        }

        if(monthInfoList.size > 3) {
            binding!!.txtDividendTitle.text = monthInfoList[3].title
            setRecyclerView(3, monthInfoList[3])
        }

        setTextColorPartial()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
}
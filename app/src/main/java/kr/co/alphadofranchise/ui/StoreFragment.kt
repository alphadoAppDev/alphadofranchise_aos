package kr.co.alphadofranchise.ui

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kr.co.alphadofranchise.R
import kr.co.alphadofranchise.base.BaseFragment
import kr.co.alphadofranchise.databinding.FragmentStoreBinding
import kr.co.alphadofranchise.models.HeadOfficeCell
import kr.co.alphadofranchise.models.HeadOfficeInnerCell
import kr.co.alphadofranchise.models.RegionCell
import kr.co.alphadofranchise.models.RegionInnerCell
import kr.co.alphadofranchise.utils.CustomBarChartRender
import kr.co.alphadofranchise.viewmodel.FranchiseViewModel
import kr.co.domain.model.FranchiseRegion
import kr.co.domain.model.ProfitInfo


class StoreFragment : BaseFragment() {
    private var storeCode: String? = null
    private var region : String? = null
    private var binding : FragmentStoreBinding? = null
    private lateinit var salesAdapter : StoreOutAdapter //협약 업체 매출현황 recyclerview
    private lateinit var revenueAdapter : StoreOutAdapter //협약 업체 수익 현황 recyclerview
    private lateinit var headOfficeSalesAdapter : StoreOutAdapter //본사 업체 매출현황 recyclerview
    private lateinit var headOfficeRevenueAdapter : StoreOutAdapter //본사 업체 수익 현황 recyclerview
    private lateinit var franchiseViewModel : FranchiseViewModel


    companion object { @JvmStatic fun newInstance(code: String, region : String) = StoreFragment()
        .apply { arguments = Bundle()
            .apply {
                putString("code", code)
                putString("region", region)
            } } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            storeCode = it.getString("code")
            region = it.getString("region")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_store, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()


//        franchiseViewModel.requestMonthProfitInfo(code = storeCode!!)
//        observeProfitInfoLiveData()
//
//        franchiseViewModel.requestRegionInfo(code = storeCode!!)
//        observeRegionInfoLiveData()
    }

    private fun init(){
        franchiseViewModel = ViewModelProvider(this, viewModelFactory).get(FranchiseViewModel::class.java)
    }


//    private fun setRecyclerView(type : Int, region : FranchiseRegion){
//        when(type) {
//            1, 2 -> {
//
//                val innerList = ArrayList<Any>()
//                region.table!!.line02?.let { innerList.add(RegionInnerCell(it)) }
//                region.table!!.line03?.let { innerList.add(RegionInnerCell(it)) }
//                region.table!!.line04?.let { innerList.add(RegionInnerCell(it)) }
//                region.table!!.line05?.let { innerList.add(RegionInnerCell(it)) }
//                region.table!!.line06?.let { innerList.add(RegionInnerCell(it)) }
//                region.table!!.line07?.let { innerList.add(RegionInnerCell(it)) }
//                region.table!!.line08?.let { innerList.add(RegionInnerCell(it)) }
//                region.table!!.line09?.let { innerList.add(RegionInnerCell(it)) }
//                region.table!!.line10?.let { innerList.add(RegionInnerCell(it)) }
//
//                val outList = ArrayList<Any>()
//                region.table!!.line01?.let { outList.add(RegionCell(it, innerList, false)) }
//
//                if(type == 1) {
//                    salesAdapter  = StoreOutAdapter(outList)
//                    binding!!.salesRecyclerView.adapter = salesAdapter
//
//                } else {
//                    revenueAdapter  = StoreOutAdapter(outList)
//                    binding!!.revenueRecyclerView.adapter = revenueAdapter
//                }
//            }
//            4, 5 ->{
//                val innerList = ArrayList<Any>()
//                region.table!!.line02?.let { innerList.add(HeadOfficeInnerCell(it)) }
//                region.table!!.line03?.let { innerList.add(HeadOfficeInnerCell(it)) }
//                region.table!!.line04?.let { innerList.add(HeadOfficeInnerCell(it)) }
//                region.table!!.line05?.let { innerList.add(HeadOfficeInnerCell(it)) }
//                region.table!!.line06?.let { innerList.add(HeadOfficeInnerCell(it)) }
//                region.table!!.line07?.let { innerList.add(HeadOfficeInnerCell(it)) }
//                region.table!!.line08?.let { innerList.add(HeadOfficeInnerCell(it)) }
//                region.table!!.line09?.let { innerList.add(HeadOfficeInnerCell(it)) }
//                region.table!!.line10?.let { innerList.add(HeadOfficeInnerCell(it)) }
//
//                val outList = ArrayList<Any>()
//                region.table!!.line01?.let { outList.add(HeadOfficeCell(it, innerList, false)) }
//
//                if(type == 4) {
//                    headOfficeSalesAdapter  = StoreOutAdapter(outList)
//                    binding!!.headOfficeSalesRecyclerView.adapter = headOfficeSalesAdapter
//
//                } else {
//                    headOfficeRevenueAdapter  = StoreOutAdapter(outList)
//                    binding!!.headOfficeRevenueRecyclerView.adapter = headOfficeRevenueAdapter
//                }
//            }
//        }
//
//
//        (activity as MainActivity).visibleProgressBar(false)
//    }
//
//    private fun observeProfitInfoLiveData(){
//        franchiseViewModel.profitInfoLiveData.observe(viewLifecycleOwner, {
//            if(it != null) {
//                if(it is String) {
//                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
//                } else {
//                    val profitInfoList = it as ArrayList<ProfitInfo>
//                    profitInfoList.forEachIndexed { index, profitInfo ->
//                        if(index == 0) {
//                            binding!!.txtCurrentStoreProfitTitle.text = profitInfo.title
//                            binding!!.txtSalesUnit00.text = profitInfo.unit
//                            binding!!.txt01.text = "매출"
//                            binding!!.txt02.text = "이익"
//                            binding!!.img00.setBackgroundResource(R.drawable.shape_circle_point)
//                            binding!!.img01.setBackgroundResource(R.drawable.shape_circle_point_sub)
//                            binding!!.imgSalesChartMenu.setBackgroundResource(R.drawable.shape_circle_point)
//                            binding!!.imgRevenueChartMenu.setBackgroundResource(R.drawable.shape_circle_point_sub)
//                            binding!!.txtCurrentStoreProfit.text = profitInfo.profit
//                            binding!!.txtCurrentStoreProfitDate.text = profitInfo.date
//                            binding!!.txtSalesChartMenu00.text = profitInfo.table.row1[1]
//                            binding!!.txtSalesChartMenu01.text = profitInfo.table.row1[2]
//                            binding!!.txtSalesChartMenu02.text = profitInfo.table.row1[3]
//                            binding!!.txtSalesChartMenu03.text = profitInfo.table.row1[4]
//                            binding!!.txtSalesChartMenu04.text = profitInfo.table.row1[5]
//                            binding!!.txtRevenueChartMenu00.text = profitInfo.table.row2[1]
//                            binding!!.txtRevenueChartMenu01.text = profitInfo.table.row2[2]
//                            binding!!.txtRevenueChartMenu02.text = profitInfo.table.row2[3]
//                            binding!!.txtRevenueChartMenu03.text = profitInfo.table.row2[4]
//                            binding!!.txtRevenueChartMenu04.text = profitInfo.table.row2[5]
//                            profitInfo.table.row0.add("")
//
//                            val maxValue = findMaxValue(profitInfo.table.row1, profitInfo.table.row2)
//                            if(maxValue != null)
//                                groupBarChart(binding!!.chartSales, profitInfo.table.row0, 1, maxValue, profitInfo.table.row1, profitInfo.table.row2)
//                        } else {
//                            binding!!.txtHeadOfficeProfitTitle.text = profitInfo.title
//                            binding!!.txtSalesUnit01.text = profitInfo.unit
//                            binding!!.txt04.text = "매출"
//                            binding!!.txt05.text = "이익"
//                            binding!!.img02.setBackgroundResource(R.drawable.shape_circle_point)
//                            binding!!.img03.setBackgroundResource(R.drawable.shape_circle_point_sub)
//                            binding!!.imgHeadOfficeSalesChartMenu.setBackgroundResource(R.drawable.shape_circle_point)
//                            binding!!.imgHeadOfficeRevenueChartMenu.setBackgroundResource(R.drawable.shape_circle_point_sub)
//                            binding!!.txtHeadOfficeProfit.text =  profitInfo.profit
//                            binding!!.txtHeadOfficeProfitDate.text = profitInfo.date
//                            binding!!.txtHeadOfficeSalesChartMenu00.text = profitInfo.table.row1[1]
//                            binding!!.txtHeadOfficeSalesChartMenu01.text = profitInfo.table.row1[2]
//                            binding!!.txtHeadOfficeSalesChartMenu02.text = profitInfo.table.row1[3]
//                            binding!!.txtHeadOfficeRevenueChartMenu00.text = profitInfo.table.row2[1]
//                            binding!!.txtHeadOfficeRevenueChartMenu01.text = profitInfo.table.row2[2]
//                            binding!!.txtHeadOfficeRevenueChartMenu02.text = profitInfo.table.row2[3]
//                            profitInfo.table.row0.add("")
//
//                            val maxValue = findMaxValue(profitInfo.table.row1, profitInfo.table.row2)
//                            if(maxValue != null)
//                                groupBarChart(binding!!.chartHeadOfficeSales, profitInfo.table.row0, 2, maxValue, profitInfo.table.row1, profitInfo.table.row2)
//                        }
//                    }
//                }
//            } else {
//                Toast.makeText(requireContext(),"인터넷 연결 끊김", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun observeRegionInfoLiveData(){
//        franchiseViewModel.regionInfoLiveData.observe(viewLifecycleOwner, {
//            if(it != null) {
//                if(it is String) {
//                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
//                } else {
//                    binding!!.txtRegion.text = region
//                    val monthInfoList = it as ArrayList<FranchiseRegion>
//
//                    if(monthInfoList.size > 0) {
//                        binding!!.txtRegionDate.text = monthInfoList[0].date?:""
//                        binding!!.txtRegionTitle.text = monthInfoList[0].title?:""
//                        monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.txt00.text = it2[0] }  }
//                        monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.storeMenu00.text = it2[1] }  }
//                        monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.storeMenu01.text = it2[2] }  }
//                        monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.storeMenu02.text = it2[3] }  }
//                        monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.storeMenu03.text = it2[4] }  }
//                        monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.storeMenu04.text = it2[5] }  }
//                        monthInfoList[0].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.storeMenu05.text = it2[6] }  }
//
//
//                        monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount.text = it2[0] } }
//                        monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount00.text = it2[1] } }
//                        monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount01.text = it2[2] } }
//                        monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount02.text = it2[3] } }
//                        monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount03.text = it2[4] } }
//                        monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount04.text = it2[5] } }
//                        monthInfoList[0].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtCompanyCount05.text = it2[6] } }
//                    }
//
//                    if(monthInfoList.size > 3) {
//                        binding!!.txtHeadOfficeDate.text = monthInfoList[3].date?:""
//                        binding!!.txtHeadOfficeTitle.text = monthInfoList[3].title?:""
//                        monthInfoList[3].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.txt03.text = it2[0] }  }
//                        monthInfoList[3].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.txtHeadOfficeMenu00.text = it2[1] }  }
//                        monthInfoList[3].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.txtHeadOfficeMenu01.text = it2[2] }  }
//                        monthInfoList[3].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.txtHeadOfficeMenu02.text = it2[3] }  }
//                        monthInfoList[3].table?.let { it1 -> it1.line01?.let { it2 ->  binding!!.txtHeadOfficeMenu03.text = it2[4] }  }
//
//
//                        monthInfoList[3].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtSalesForYear.text = it2[0] } }
//                        monthInfoList[3].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtSalesForYearMenu00.text = it2[1] } }
//                        monthInfoList[3].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtSalesForYearMenu01.text = it2[2] } }
//                        monthInfoList[3].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtSalesForYearMenu02.text = it2[3] } }
//                        monthInfoList[3].table?.let { it1 -> it1.line02?.let { it2 -> binding!!.txtSalesForYearMenu03.text = it2[4] } }
//                    }
//
//
//
//
//                    if(monthInfoList.size > 0) {
//                        monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales.text = it2[0] } }
//                        monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales00.text = it2[1] } }
//                        monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales01.text = it2[2] } }
//                        monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales02.text = it2[3] } }
//                        monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales03.text = it2[4] } }
//                        monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales04.text = it2[5] } }
//                        monthInfoList[0].table?.let { it1 -> it1.line03?.let { it2 -> binding!!.txtSales05.text = it2[6] } }
//                    }
//
//
//                    if(monthInfoList.size > 1)  {
//                        binding!!.txtRegionMonthSalesTitle.text = monthInfoList[1].title
//                        setRecyclerView(1, monthInfoList[1])
//                    }
//                    if(monthInfoList.size > 2) {
//                        binding!!.txtRegionMonthProfitTitle.text = monthInfoList[2].title
//                        setRecyclerView(2, monthInfoList[2])
//                    }
//                    if(monthInfoList.size > 4) {
//                        binding!!.txtHeadOfficeMonthSales.text = monthInfoList[4].title
//                        setRecyclerView(4, monthInfoList[4])
//                    }
//                    if(monthInfoList.size > 5){
//                        binding!!.txtHeadOfficeMonthProfit.text = monthInfoList[5].title
//                        setRecyclerView(5, monthInfoList[5])
//                    }
//
//                    setTextColorPartial()
//                }
//            } else {
//                Toast.makeText(requireContext(),"인터넷 연결 끊김", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun setTextColorPartial() {
//        var sp = SpannableStringBuilder(binding!!.txtSales.text.toString())
//        sp.setSpan(ForegroundColorSpan(getColor(requireContext(), R.color.font_sub)), 9, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        sp.setSpan(RelativeSizeSpan(0.83f), 9,14, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
//        binding!!.txtSales.text = sp
//
//
//        sp = SpannableStringBuilder(binding!!.txtSalesForYear.text.toString())
//        sp.setSpan(ForegroundColorSpan(getColor(requireContext(), R.color.font_sub)), 9, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        sp.setSpan(RelativeSizeSpan(0.83f), 9,14, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
//        binding!!.txtSalesForYear.text = sp
//
//        sp = SpannableStringBuilder(binding!!.txtRegionMonthProfitTitle.text.toString())
//        sp.setSpan(ForegroundColorSpan(getColor(requireContext(), R.color.font_sub)), 5, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        sp.setSpan(RelativeSizeSpan(0.83f), 5,10, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
//        binding!!.txtRegionMonthProfitTitle.text = sp
//
//        sp = SpannableStringBuilder(binding!!.txtHeadOfficeMonthProfit.text.toString())
//        sp.setSpan(ForegroundColorSpan(getColor(requireContext(), R.color.font_sub)), 5, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        sp.setSpan(RelativeSizeSpan(0.83f), 5,10, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
//        binding!!.txtHeadOfficeMonthProfit.text = sp
//    }
//
//    private fun findMaxValue(listRow1 : ArrayList<String>, listRow2 : ArrayList<String>): Float? {
//        try {
//            var row1Max : Float = listRow1[1].replace(",","").toFloat()
//            listRow1.forEachIndexed { index, s ->
//                if(index == 0) return@forEachIndexed
//                else {
//                    row1Max = row1Max.coerceAtLeast(s.replace(",", "").toFloat())
//                }
//            }
//
//            var row2Max : Float = listRow1[1].replace(",","").toFloat()
//            listRow2.forEachIndexed { index, s ->
//                if(index == 0) return@forEachIndexed
//                else {
//                    row2Max = row2Max.coerceAtLeast(s.replace(",", "").toFloat())
//                }
//            }
//            return row1Max.coerceAtLeast(row2Max)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return null
//        }
//
//    }
//
//    private fun groupBarChart(barChart : BarChart, labels : ArrayList<String>, type : Int, maxValue : Float, valOne : ArrayList<String>, valTwo: ArrayList<String>) {
//        barChart.apply {
//            setDrawBarShadow(false)
//            description.isEnabled = false
//            setPinchZoom(false)
//            setDrawGridBackground(false)
//            legend.isEnabled = false
////            legend.apply {
////                isEnabled = true
////                textSize = 10f
////                form = Legend.LegendForm.CIRCLE
////                xOffset = 10f
////                yOffset = 15f
////                xEntrySpace = 10f
////                verticalAlignment = Legend.LegendVerticalAlignment.TOP
////                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
////            }
//            val barChartRender = CustomBarChartRender(barChart, barChart.animator, barChart.viewPortHandler)
//            barChartRender.setRadius(15)
//            renderer = barChartRender
//        }
//
//        barChart.xAxis.apply {
//            setCenterAxisLabels(true)
//            position = XAxis.XAxisPosition.BOTTOM
//            granularity = 1f
//            textColor = Color.BLACK
//            textSize = 12f
//            axisLineColor = getColor(requireContext(), R.color.stroke_grey)
//            axisLineWidth = 0.5f
//            setDrawGridLines(false)
//            setDrawAxisLine(true)
//            labelCount = labels.size
//            valueFormatter = IndexAxisValueFormatter(labels)
//            axisMinimum = 0.9f
//            axisMaximum = labels.size - 0.9f
//            yOffset = 3f
//        }
//
//        barChart.axisLeft.run { // 왼쪽 y축
//            isEnabled = false
//            axisMinimum = 0f // 최소값
//            axisMaximum = maxValue // 최대값
//            granularity = 10f // 값 만큼 라인선 설정
//            setDrawLabels(false) // 값 셋팅 설정
//            textColor = Color.RED // 색상 설정
//        }
//        barChart.axisRight.run { // 오른쪽 y축(왼쪽과동일)
//            isEnabled = false
//            textSize = 15f
//        }
//
//        val barOne: ArrayList<BarEntry> = ArrayList()
//        val barTwo: ArrayList<BarEntry> = ArrayList()
//
//        valOne.forEachIndexed { index, s ->
//            if (index == 0) return@forEachIndexed
//            else {
//                barOne.add(BarEntry(index.toFloat(), valOne[index].replace(",","").toFloat()))
//                barTwo.add(BarEntry(index.toFloat(), valTwo[index].replace(",","").toFloat()))
//            }
//        }
//
//
//        val set1 = BarDataSet(barOne, "매출")
//        set1.color = getColor(requireContext(), R.color.point_color)
//        val set2 = BarDataSet(barTwo, "이익")
//        set2.color = getColor(requireContext(), R.color.point_sub)
//
//        set1.isHighlightEnabled = false
//        set2.isHighlightEnabled = false
//        set1.setDrawValues(false)
//        set2.setDrawValues(false)
//
//        val dataSets = ArrayList<IBarDataSet>()
//        dataSets.add(set1)
//        dataSets.add(set2)
//
//        val data = BarData(dataSets)
//        val groupSpace : Float
//        val barSpace : Float
//        val barWidth : Float
//
//        if(type == 1) {
//            groupSpace = 0.42f //45
//            barSpace  = 0.11f //11
//            barWidth = 0.18f  //18
//        } else {
//            groupSpace = 0.66f
//            barSpace  = 0.07f
//            barWidth = 0.1f
//        }
//
//
//        data.barWidth = barWidth
//
//        barChart.data = data
//        barChart.setScaleEnabled(false)
//        barChart.setVisibleXRangeMaximum(6f)
//        barChart.groupBars(1f, groupSpace, barSpace)
//        barChart.invalidate()
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//
//        binding = null
//        compositeDisposable.clear()
//    }
}
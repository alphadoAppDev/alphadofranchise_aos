package kr.co.alphadofranchise.ui.tab00

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.robinhood.ticker.TickerUtils
import kr.co.alphadofranchise.R
import kr.co.alphadofranchise.base.BaseFragment
import kr.co.alphadofranchise.databinding.FragmentFranchiseBinding
import kr.co.alphadofranchise.models.FranchiseSummaryInfo
import kr.co.alphadofranchise.ui.MainActivity
import kr.co.alphadofranchise.viewmodel.FranchiseViewModel
import kr.co.domain.model.FranchiseRegion


class FranchiseFragment : BaseFragment() {
    private var binding : FragmentFranchiseBinding? = null
    private lateinit var franchiseViewModel : FranchiseViewModel
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var storeCode: String? = null
    private var region : String? = null
    private var resumed = false
    private var monthInfoList : ArrayList<FranchiseRegion>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            storeCode = it.getString("code")
            region = it.getString("region")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(code: String, region : String) =
            FranchiseFragment().apply {
                arguments = Bundle().apply {
                    putString("code", code)
                    putString("region", region)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFranchiseBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        franchiseViewModel.requestFranchiseSummaryInfo(code = storeCode!!)
        observeFranchiseSummaryInfoLiveData()

        franchiseViewModel.requestRegionInfo2(code = storeCode!!)
        observeRegionInfoLiveData()
    }

    private fun init(){
        franchiseViewModel = ViewModelProvider(this, viewModelFactory).get(FranchiseViewModel::class.java)
        binding!!.fragment = this
        monthInfoList = null
        binding!!.txtRegion.text =  region
        binding!!.txtCurrentProfit.setCharacterLists(TickerUtils.provideNumberList())
        binding!!.txtTotalProfit.setCharacterLists(TickerUtils.provideNumberList())


        (activity as MainActivity).visibleProgressBar(false)
    }

    private fun observeFranchiseSummaryInfoLiveData(){
        franchiseViewModel.summaryInfoLiveData.observe(viewLifecycleOwner, {
            if(it != null){
                if(it is String){
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                } else {
                    val summaryInfo = it as FranchiseSummaryInfo
                    binding!!.txtRegion.text = summaryInfo.region
                    binding!!.txtCurrentProfit.visibility = View.VISIBLE
                    binding!!.txtCurrentProfitTitle.text = "${summaryInfo.month} 현재 수익"
                    binding!!.txtCurrentProfit.text = summaryInfo.month_profit

                    binding!!.txtTotalProfit.visibility = View.VISIBLE
                    binding!!.txtTotalProfit.text = summaryInfo.total_profit
                    binding!!.txtTotalStoreCount.text = "${summaryInfo.total_count}"

                    binding!!.txtHospitalCount.text = summaryInfo.hospital_count
                    binding!!.txtBeautyCount.text = summaryInfo.beauty_count
                    binding!!.txtKinderGardenCount.text = summaryInfo.kindergarten_count
                    binding!!.txtCafeCount.text = summaryInfo.cafe_count
                    binding!!.txtFuneralCount.text = summaryInfo.funeral_count

                    binding!!.visibleView.visibility = View.GONE
                }
            } else {
                Toast.makeText(requireContext(),"인터넷 연결 끊김", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun observeRegionInfoLiveData(){
        franchiseViewModel.regionInfoLiveData.observe(viewLifecycleOwner, {
            if(it != null) {
                if(it is String) {

                } else {
                    binding!!.txtRegion.text = region
                    monthInfoList = it as ArrayList<FranchiseRegion>
                }
            } else {}
        })
    }

    private fun createRunnable(): Runnable {
        return Runnable {
            if (resumed) {
                onUpdate()
            }
        }
    }

    protected fun onUpdate() {
//        binding!!.txtCurrentProfit.visibility = View.VISIBLE
//        binding!!.txtCurrentProfit.text = "12,345,678"
//
//        binding!!.txtTotalProfit.visibility = View.VISIBLE
//        binding!!.txtTotalProfit.text = "45,345,678"

    }

    fun startActivityMoveContractedCompanyActivity(){
        val intent = Intent(requireActivity(), ContractedCompanyActivity::class.java)
        intent.putExtra("code",storeCode)
        intent.putExtra("region",region)
        monthInfoList?.let { intent.putExtra("monthList",it) }
        startActivity(intent)
        (activity as MainActivity).overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
    }

    fun startActivityMoveHeadOfficeActivity(){
        val intent = Intent(requireActivity(), HeadOfficeActivity::class.java)
        intent.putExtra("code",storeCode)
        intent.putExtra("region",region)
        monthInfoList?.let { intent.putExtra("monthList",it) }
        startActivity(intent)
        (activity as MainActivity).overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
    }


    override fun onResume() {
        super.onResume()

        resumed = true
        handler.post(createRunnable())
    }
}
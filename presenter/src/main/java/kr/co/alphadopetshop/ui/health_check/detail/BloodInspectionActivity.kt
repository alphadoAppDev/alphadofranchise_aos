package kr.co.alphadopetshop.ui.health_check.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.ActivityBloodInspectionBinding
import kr.co.alphadopetshop.model.HealthCheckItem
import kr.co.alphadopetshop.model.InspectionItem
import kr.co.domain.model.HospitalCheckup

class BloodInspectionActivity : AppCompatActivity() {
    private lateinit var inspectionAdapter: InspectionAdapter
    private var binding: ActivityBloodInspectionBinding? = null
    private lateinit var hospitalCheckup: HospitalCheckup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_blood_inspection)

        init()
        Log.d("test","hospitalCheckup : ${hospitalCheckup.checkup_infos}")
        setBloodInspectionRecycler()

    }

    private fun init() {
        binding!!.activity = this
        hospitalCheckup =
            intent.getBundleExtra("bundle")?.getSerializable("hospital_checkup") as HospitalCheckup
        binding!!.txtCheckupProcess.text = hospitalCheckup.checkup_process
        binding!!.txtCheckupTime.text = "${hospitalCheckup.checkup_time}"
        binding!!.txtCheckupDevice.text = hospitalCheckup.checkup_device
    }

    private fun setBloodInspectionRecycler() {
        inspectionAdapter = InspectionAdapter()
        hospitalCheckup.checkup_infos.forEachIndexed { i, element ->
            if(element.title.trim().isEmpty()) return@forEachIndexed
            inspectionAdapter.addItem(
                InspectionItem(
                    i + 1,
                    hospitalCheckup.checkup_infos[i].title,
                    hospitalCheckup.checkup_infos[i].description
                )
            )
        }


        binding!!.bloodInspectionRecycler.adapter = inspectionAdapter
    }
}
package kr.co.alphadopetshop.ui.health_check.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.ActivityUltrasonicWaveInspectionBinding
import kr.co.alphadopetshop.model.InspectionItem
import kr.co.domain.model.HospitalCheckup

class UltrasonicWaveInspectionActivity : AppCompatActivity() {
    private var binding : ActivityUltrasonicWaveInspectionBinding? = null
    private lateinit var inspectionAdapter : InspectionAdapter
    private lateinit var hospitalCheckup : HospitalCheckup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ultrasonic_wave_inspection)

        init()
        setUltrasonicWaveInspectionRecycler()
    }

    private fun init() {
        binding!!.activity = this
        hospitalCheckup =
            intent.getBundleExtra("bundle")?.getSerializable("hospital_checkup") as HospitalCheckup
        binding!!.txtCheckupProcess.text = hospitalCheckup.checkup_process
        binding!!.txtCheckupTime.text = "${hospitalCheckup.checkup_time}"
        binding!!.txtCheckupDevice.text = hospitalCheckup.checkup_device
    }

    private fun setUltrasonicWaveInspectionRecycler(){
        inspectionAdapter = InspectionAdapter()

        hospitalCheckup.checkup_infos.forEachIndexed { i, element ->
            if(hospitalCheckup.checkup_infos[i].title.trim().isEmpty()) return@forEachIndexed
            inspectionAdapter.addItem(
                InspectionItem(
                    i + 1,
                    hospitalCheckup.checkup_infos[i].title ?: "",
                    hospitalCheckup.checkup_infos[i].description ?: ""
                )
            )
        }

        binding!!.ultrasonicWaveInspectionRecycler.adapter = inspectionAdapter
    }
}
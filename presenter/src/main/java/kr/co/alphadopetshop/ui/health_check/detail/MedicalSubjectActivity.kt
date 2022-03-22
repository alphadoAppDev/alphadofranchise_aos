package kr.co.alphadopetshop.ui.health_check.detail

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import kr.co.alphadopetshop.BaseActivity
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.ActivityMedicalSubjectBinding
import kr.co.alphadopetshop.util.CommonUtils
import kr.co.domain.model.MedicalSubject
import kotlin.math.roundToInt

class MedicalSubjectActivity : BaseActivity() {
    private var binding : ActivityMedicalSubjectBinding? = null
    private lateinit var medicalSubject : MedicalSubject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_medical_subject)

        init()
    }

    private fun init(){
        binding!!.activity = this
        binding!!.btnBack.setOnClickListener { finish() }
        medicalSubject = (intent.getBundleExtra("bundle")?.get("medical_subject")) as MedicalSubject
        binding!!.txtTitle.text = medicalSubject.title

        Glide.with(this)
            .load(Uri.parse(medicalSubject.url))
            .into(binding!!.imgMedicalSubject)


    }
}
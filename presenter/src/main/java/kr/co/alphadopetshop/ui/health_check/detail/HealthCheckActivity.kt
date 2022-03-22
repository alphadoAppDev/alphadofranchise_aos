package kr.co.alphadopetshop.ui.health_check.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.ActivityHealthCheckBinding
import kr.co.alphadopetshop.databinding.ItemHealthCheckBinding
import kr.co.alphadopetshop.model.HealthCheckItem
import kr.co.domain.model.HospitalCheckup

class HealthCheckActivity : AppCompatActivity() {
    private var binding : ActivityHealthCheckBinding? = null
    private lateinit var adapter : HealthCheckAdapter
    private lateinit var hospitalCheckup : HospitalCheckup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_health_check)

        init()
        setHealthCheckRecycler()
    }

    private fun init(){
        binding!!.activity = this
        hospitalCheckup = intent.getBundleExtra("bundle")?.getSerializable("hospital_checkup") as HospitalCheckup
        binding!!.txtCheckupProcess.text = hospitalCheckup.checkup_process
        binding!!.txtCheckupTime.text = "${hospitalCheckup.checkup_time}"
        binding!!.txtCheckupDevice.text = hospitalCheckup.checkup_device
    }

    private fun setHealthCheckRecycler(){
        adapter = HealthCheckAdapter()
        hospitalCheckup.checkup_infos.forEach { element ->
            if(element.title.trim().isEmpty()) return@forEach
            adapter.addItem(HealthCheckItem(parsePartToKorean(element.title), element.description))
        }
        binding!!.healthCheckRecycler.adapter = adapter
    }

    private fun parsePartToKorean(part : String) : String {
        return when(part) {
            "eyes" -> "눈"
            "ears" -> "귀"
            "mouth" -> "입"
            "heart" -> "심장"
            "lung" -> "폐"
            "abdomen" -> "복부"
            "legs" -> "사지"
            else -> "꼬리의 시작부위"
        }
    }
 }

class HealthCheckAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val items = ArrayList<HealthCheckItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemHealthCheckBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_health_check,
            parent,
            false
        )
        return HealthCheckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HealthCheckViewHolder).bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item : HealthCheckItem) {
        items.add(item)
    }

    class HealthCheckViewHolder(private val binding : ItemHealthCheckBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item : HealthCheckItem){
            when(item.part) {
                "눈" -> binding.imgPart.setImageResource(R.drawable.icon_hospital_eyes)
                "귀" -> binding.imgPart.setImageResource(R.drawable.icon_hospital_ear)
                "입" -> binding.imgPart.setImageResource(R.drawable.icon_hospital_mouth)
                "심장" -> binding.imgPart.setImageResource(R.drawable.icon_hospital_heart)
                "폐" -> binding.imgPart.setImageResource(R.drawable.icon_hospital_lung)
                "복부" -> binding.imgPart.setImageResource(R.drawable.icon_hospital_abdominal)
                "사지" -> binding.imgPart.setImageResource(R.drawable.icon_hospital_course)
                "꼬리의 시작부위" -> binding.imgPart.setImageResource(R.drawable.icon_hospital_tail)
            }

            binding.txtPart.text = item.part
            binding.txtContent.text = item.content
        }
    }
}
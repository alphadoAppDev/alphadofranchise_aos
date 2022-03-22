package kr.co.alphadopetshop.ui.health_check.detail

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kr.co.alphadopetshop.ImageLoadThread
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.ActivityHospitalImageBinding
import kr.co.alphadopetshop.databinding.ItemHospitalImageBinding
import kr.co.alphadopetshop.databinding.ItemHospitalImageContainerBinding
import kr.co.alphadopetshop.model.HospitalImageContainer
import kr.co.alphadopetshop.util.CommonUtils
import kr.co.domain.model.HospitalImage
import java.lang.Math.abs

class HospitalImageActivity : AppCompatActivity() {
    private var binding : ActivityHospitalImageBinding? = null
    private lateinit var hospitalImageAdapter : HospitalImageContainerAdapter
    private lateinit var hospitalImages : ArrayList<HospitalImage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hospital_image)
        init()

        setImageRecyclerView()
    }

    private fun init(){
        hospitalImages = (intent.getBundleExtra("bundle")?.getSerializable("hospital_image")) as ArrayList<HospitalImage>
        binding!!.btnBack.setOnClickListener { finish() }
    }

    private fun setImageRecyclerView() {
        val set = HashSet<Int>()
        for(element in hospitalImages) {
            set.add(parseTypeToOrder(element.type!!))
        }

        val list = Array(8) { HospitalImageContainer() }
        hospitalImages.forEach { element ->
            if(element.url?.trim()?.isEmpty() == true) return@forEach
            list[parseTypeToOrder(element.type!!)].urls.add(element.url!!)
            list[parseTypeToOrder(element.type!!)].type = element.type!!
        }
        val newList = ArrayList<HospitalImageContainer>()
        list.forEach {
            if(it.type.isNotEmpty()) {
                newList.add(it)
            }
        }

        hospitalImageAdapter = HospitalImageContainerAdapter(newList)
        binding!!.recyclerHospital.adapter = hospitalImageAdapter
    }

    private fun parseTypeToOrder(type : String) : Int{
        return when {
            type.contains("main_img") -> {
                0
            }
            type.contains("exterior_img") -> {
                1
            }
            type.contains("interior_img") -> {
                2
            }
            type.contains("room_img") -> {
                3
            }
            type.contains("surgery_img") -> {
                4
            }
            type.contains("recovery_img") -> {
                5
            }
            type.contains("rest_img") -> {
                6
            }
            else -> {
                7
            }
        }
    }
}

class HospitalImageContainerAdapter(
    private val items :  ArrayList<HospitalImageContainer>
) : RecyclerView.Adapter<HospitalImageContainerAdapter.HospitalImageContainerViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalImageContainerViewHolder {
        val binding = DataBindingUtil.inflate<ItemHospitalImageContainerBinding>(LayoutInflater.from(parent.context),R.layout.item_hospital_image_container, parent, false)
        return HospitalImageContainerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HospitalImageContainerViewHolder, position: Int) {
        holder.bind(items.get(position))
    }

    override fun getItemCount(): Int = items.size


    class HospitalImageContainerViewHolder(
        private val binding : ItemHospitalImageContainerBinding
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item : HospitalImageContainer){
            binding.txtTitle.text = CommonUtils.parseTypeToKorean(item.type)

            val offsetBetweenPages = binding.root.context.resources.getDimensionPixelOffset(R.dimen.offsetBetweenPages).toFloat()
            binding.imgViewPager2.offscreenPageLimit = 3
            binding.imgViewPager2.setPageTransformer { page, position ->
                val myOffset = position * -(2 * offsetBetweenPages)
                if (position < -1) {
                    page.translationX = -myOffset
                } else if (position <= 1) {
                    // Paging 시 Y축 Animation 배경색을 약간 연하게 처리
                    val scaleFactor = 0.8f.coerceAtLeast(1 - abs(position))
                    page.translationX = myOffset
//                    page.scaleY = scaleFactor
//                    page.alpha = scaleFactor
                } else {
//                    page.alpha = 0f
                    page.translationX = myOffset
                }
            }

            val imageAdapter = HospitalImageAdapter(item.urls)
            binding.imgViewPager2.adapter = imageAdapter
            binding.imgViewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            binding.imgViewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    binding.txtPageCount.text = "${position + 1}/${item.urls.size}"
                }
            })
        }
    }
}

class HospitalImageAdapter(
    private val urls : ArrayList<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemHospitalImageBinding>(LayoutInflater.from(parent.context),R.layout.item_hospital_image, parent, false)
        return HospitalImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HospitalImageViewHolder).bind(urls[position])
    }

    override fun getItemCount(): Int = urls.size

    class HospitalImageViewHolder(
        private val binding : ItemHospitalImageBinding
    ) : RecyclerView.ViewHolder(binding.root){
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
                        .into(binding.imgHospital)
                }
            }
        }


        fun bind(item : String){
            if(item.isNotEmpty())
                ImageLoadThread(mHandler, item).start()
        }
    }
}
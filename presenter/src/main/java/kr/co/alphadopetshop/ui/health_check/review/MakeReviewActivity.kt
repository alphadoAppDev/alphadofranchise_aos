package kr.co.alphadopetshop.ui.health_check.review

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kr.co.alphadopetshop.BaseActivity
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.ActivityMakeReviewBinding
import kr.co.alphadopetshop.model.ReviewModel
import kr.co.alphadopetshop.HospitalViewModel
import kr.co.alphadopetshop.util.CommonUtils
import kotlin.collections.ArrayList

class MakeReviewActivity : BaseActivity(), View.OnClickListener {
    private var binding : ActivityMakeReviewBinding? = null
    private lateinit var makeReviewFragment00 : MakeReviewFragment00
    private lateinit var makeReviewFragment01 : MakeReviewFragment01
    private var reviewViewPagerAdapter : ReviewViewPagerAdapter? = null
    private var reviewModel  = ReviewModel()
    private lateinit var hospitalViewModel : HospitalViewModel
    private var hospitalId : Int? = null
    private var urls = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_make_review)

        init()
        setViewPager2()
        observeUploadImageLiveData()
        observeAddReviewLiveData()
        observeProgressBar()
    }

    private fun init(){
        hospitalViewModel = ViewModelProvider(this, viewModelFactory).get(HospitalViewModel::class.java)
        hospitalId = intent.getIntExtra("hospital_id", 0)
        makeReviewFragment00 = MakeReviewFragment00.newInstance()
        makeReviewFragment01 = MakeReviewFragment01.newInstance()
        binding!!.vpContent.isUserInputEnabled = false
        binding!!.vpContent.offscreenPageLimit = 2
        binding!!.btnBack.setOnClickListener(this)
        binding!!.btnBottom.setOnClickListener(this)
    }

    private fun setViewPager2(){
        reviewViewPagerAdapter = ReviewViewPagerAdapter(this, 2, makeReviewFragment00, makeReviewFragment01)
        binding!!.vpContent.adapter = reviewViewPagerAdapter

        binding!!.vpContent.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if(position == 0) {
                    binding!!.btnBack.setImageResource(R.drawable.icon_close_black)
                    binding!!.editFocus.isFocusable = true
                    binding!!.editFocus.requestFocus()
                    CommonUtils.hideKeyboard(applicationContext, binding!!.editFocus)
                    checkSatisfaction()
                    binding!!.btnBottom.text = "다음"
                } else {
                    binding!!.btnBack.setImageResource(R.drawable.icon_previous_black)
//                    makeReviewFragment01.setRequestEditText()
                    binding!!.btnBottom.text = "작성 완료"

                    if(makeReviewFragment01.checkValidReview()){
                        binding!!.btnBottom.setBackgroundColor(getColor(R.color.point_color))
                        binding!!.btnBottom.setTextColor(getColor(R.color.white))
                    } else {
                        binding!!.btnBottom.setBackgroundColor(getColor(R.color.button_background_grey))
                        binding!!.btnBottom.setTextColor(getColor(R.color.font_title))
                    }
                }
            }
        })
    }

    private fun observeUploadImageLiveData(){
        hospitalViewModel.reviewImageLiveData.observe(this, {
            urls = it
            hospitalViewModel.requestAddReview(
                hospitalId =  hospitalId!!, writer = "writer", description = reviewModel.review!!, facilityScore = reviewModel.facilitySatisfaction!!,
                examinationScore = reviewModel.examinationSatisfaction!!, kindnessScore = reviewModel.kindnessSatisfaction!!, likeCount = 0, createdAt = "",it!!
            )
        })
    }

    private fun observeAddReviewLiveData(){
        hospitalViewModel.addReviewLiveData.observe(this, {
            if(it != null) {
                if(it){
                    Toast.makeText(this, getString(R.string.review_complete), Toast.LENGTH_SHORT).show()

//                    val images =  ArrayList<ReviewImages>()
//                    urls.forEach { element ->
//                        images.add(ReviewImages(url = element, id = null, review_id = null))
//                    }
//                    val scores = ArrayList<ReviewScore>()
//                    scores.add(ReviewScore(hospital_id = hospitalId!!, type = "hospital", reviewModel.facilitySatisfaction!!, id = null, review_id = null))
//                    scores.add(ReviewScore(hospital_id = hospitalId!!, type = "satisfaction", reviewModel.examinationSatisfaction!!, id = null, review_id = null))
//                    scores.add(ReviewScore(hospital_id = hospitalId!!, type = "kindness", reviewModel.kindnessSatisfaction!!, id = null, review_id = null))
//
//                    val todayCal = Calendar.getInstance()
//                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//                    val hospitalReviewItem = HospitalReviewItem(
//                        reviewContent = ReviewContent(hospital_id = hospitalId!!, writer = "writer", description = reviewModel.review!!, null, 0, formatter.format(todayCal.time)),
//                        reviewImages = images,
//                        reviewScores = scores
//                    )
//
                    val intent = Intent()
//                    intent.putExtra("hospital_review_item", hospitalReviewItem)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        })
    }

    private fun observeProgressBar(){
        hospitalViewModel.mIsLoading.observe(this, {
            if(it) {
                binding!!.progressBar.visibility = View.VISIBLE
            } else {
                binding!!.progressBar.visibility = View.GONE
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnBack -> {
                if(binding!!.vpContent.currentItem == 0) {
                    finish()
                } else {
                    binding!!.vpContent.currentItem = 0
                }
            }

            R.id.btnBottom -> {
                if(binding!!.vpContent.currentItem == 0) {
                    if(validCheck(0)) binding!!.vpContent.currentItem = 1
                } else {
                    if(validCheck(1)) {
                        val imageList = makeReviewFragment01.getImagesFile()
                        if(imageList.size > 0){
                            hospitalViewModel.requestUploadImage(imageList)
                        } else{
                            hospitalViewModel.requestAddReview(
                                hospitalId =  hospitalId!!, writer = "writer", description = reviewModel.review!!, facilityScore = reviewModel.facilitySatisfaction!!,
                                examinationScore = reviewModel.examinationSatisfaction!!, kindnessScore = reviewModel.kindnessSatisfaction!!, 0, "", ArrayList()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun validCheck(page : Int) : Boolean{
        if(page == 0) {
            if(reviewModel.examinationSatisfaction == null) {
                Toast.makeText(this, getString(R.string.review_valid_check00), Toast.LENGTH_SHORT).show()
                return false
            } else if(reviewModel.facilitySatisfaction == null){
                Toast.makeText(this, getString(R.string.review_valid_check01), Toast.LENGTH_SHORT).show()
                return false
            } else if(reviewModel.kindnessSatisfaction == null){
                Toast.makeText(this, getString(R.string.review_valid_check02), Toast.LENGTH_SHORT).show()
                return false
            }
        } else {
            if(!makeReviewFragment01.checkValidReview()) {
                Toast.makeText(this, getString(R.string.review_valid_check03), Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }


    fun setExaminationSatisfaction(num : Int){
        reviewModel.examinationSatisfaction = num
        checkSatisfaction()
    }

    fun setFacilitySatisfaction(num : Int){
        reviewModel.facilitySatisfaction = num
        checkSatisfaction()
    }

    fun setKindnessSatisfaction(num : Int){
        reviewModel.kindnessSatisfaction = num
        checkSatisfaction()
    }

    fun setReview(review : String){
        reviewModel.review = review
    }

    fun inActivateButton(){
        binding!!.btnBottom.setBackgroundColor(getColor(R.color.button_background_grey))
        binding!!.btnBottom.setTextColor(getColor(R.color.font_title))
    }

    fun activateButton(){
        binding!!.btnBottom.setBackgroundColor(getColor(R.color.point_color))
        binding!!.btnBottom.setTextColor(getColor(R.color.white))
    }

    fun checkSatisfaction(){
        if(reviewModel.examinationSatisfaction != null
            && reviewModel.facilitySatisfaction != null
            && reviewModel.kindnessSatisfaction != null) {
            binding!!.btnBottom.setBackgroundColor(getColor(R.color.point_color))
            binding!!.btnBottom.setTextColor(getColor(R.color.white))
        }
    }

}

class ReviewViewPagerAdapter(
    fa : FragmentActivity,
    private val pageNumber : Int,
    private val makeReviewFragment00 : MakeReviewFragment00,
    private val makeReviewFragment01 : MakeReviewFragment01
) : FragmentStateAdapter(fa) {


    override fun getItemCount(): Int = pageNumber

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> makeReviewFragment00
            else -> makeReviewFragment01
        }
    }
}
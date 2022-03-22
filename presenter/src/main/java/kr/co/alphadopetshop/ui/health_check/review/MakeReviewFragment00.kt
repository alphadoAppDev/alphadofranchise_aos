package kr.co.alphadopetshop.ui.health_check.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import kr.co.alphadopetshop.BaseFragment
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.FragmentMakeReview00Binding


class MakeReviewFragment00 : BaseFragment() {
    private var binding : FragmentMakeReview00Binding? = null

    companion object { @JvmStatic fun newInstance() = MakeReviewFragment00() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_make_review00, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init(){
        binding!!.fragment = this
    }


    fun selectSatisfaction(num : Int, selectedNum : Int){
        when(num){
            0 -> {
                (activity as MakeReviewActivity).setExaminationSatisfaction(selectedNum)
                setExamination(selectedNum)
            }
            1 -> {
                (activity as MakeReviewActivity).setFacilitySatisfaction(selectedNum)
                setFacility(selectedNum)
            }
            2 -> {
                (activity as MakeReviewActivity).setKindnessSatisfaction(selectedNum)
                setKindness(selectedNum)
            }
        }
    }

    private fun setExamination(selectedNum : Int){
        binding!!.examinationSatisfaction00.setImageResource(R.drawable.icon_review_very_bad_gray)
        binding!!.examinationSatisfaction01.setImageResource(R.drawable.icon_review_bad_gray)
        binding!!.examinationSatisfaction02.setImageResource(R.drawable.icon_review_soso_gray)
        binding!!.examinationSatisfaction03.setImageResource(R.drawable.icon_review_good_gray)
        binding!!.examinationSatisfaction04.setImageResource(R.drawable.icon_review_very_good_gray)

        when(selectedNum) {
            1 -> { binding!!.examinationSatisfaction00.setImageResource(R.drawable.icon_review_very_bad_purple) }
            2 -> { binding!!.examinationSatisfaction01.setImageResource(R.drawable.icon_review_bad_purple) }
            3 -> { binding!!.examinationSatisfaction02.setImageResource(R.drawable.icon_review_soso_purple) }
            4 -> { binding!!.examinationSatisfaction03.setImageResource(R.drawable.icon_review_good_purple) }
            5 -> { binding!!.examinationSatisfaction04.setImageResource(R.drawable.icon_review_very_good_purple) }
        }
    }

    private fun setFacility(selectedNum : Int){
        binding!!.facilitySatisfaction00.setImageResource(R.drawable.icon_review_very_bad_gray)
        binding!!.facilitySatisfaction01.setImageResource(R.drawable.icon_review_bad_gray)
        binding!!.facilitySatisfaction02.setImageResource(R.drawable.icon_review_soso_gray)
        binding!!.facilitySatisfaction03.setImageResource(R.drawable.icon_review_good_gray)
        binding!!.facilitySatisfaction04.setImageResource(R.drawable.icon_review_very_good_gray)

        when(selectedNum) {
            1 -> { binding!!.facilitySatisfaction00.setImageResource(R.drawable.icon_review_very_bad_purple) }
            2 -> { binding!!.facilitySatisfaction01.setImageResource(R.drawable.icon_review_bad_purple) }
            3 -> { binding!!.facilitySatisfaction02.setImageResource(R.drawable.icon_review_soso_purple) }
            4 -> { binding!!.facilitySatisfaction03.setImageResource(R.drawable.icon_review_good_purple) }
            5 -> { binding!!.facilitySatisfaction04.setImageResource(R.drawable.icon_review_very_good_purple) }
        }
    }

    private fun setKindness(selectedNum : Int){
        binding!!.kindnessSatisfaction00.setImageResource(R.drawable.icon_review_very_bad_gray)
        binding!!.kindnessSatisfaction01.setImageResource(R.drawable.icon_review_bad_gray)
        binding!!.kindnessSatisfaction02.setImageResource(R.drawable.icon_review_soso_gray)
        binding!!.kindnessSatisfaction03.setImageResource(R.drawable.icon_review_good_gray)
        binding!!.kindnessSatisfaction04.setImageResource(R.drawable.icon_review_very_good_gray)

        when(selectedNum) {
            1 -> { binding!!.kindnessSatisfaction00.setImageResource(R.drawable.icon_review_very_bad_purple) }
            2 -> { binding!!.kindnessSatisfaction01.setImageResource(R.drawable.icon_review_bad_purple) }
            3 -> { binding!!.kindnessSatisfaction02.setImageResource(R.drawable.icon_review_soso_purple) }
            4 -> { binding!!.kindnessSatisfaction03.setImageResource(R.drawable.icon_review_good_purple) }
            5 -> { binding!!.kindnessSatisfaction04.setImageResource(R.drawable.icon_review_very_good_purple) }
        }
    }


}
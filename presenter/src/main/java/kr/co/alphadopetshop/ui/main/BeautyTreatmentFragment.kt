package kr.co.alphadopetshop.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import kr.co.alphadopetshop.BaseFragment
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.FragmentBeautyTreatmentBinding


class BeautyTreatmentFragment : BaseFragment() {
    private var binding : FragmentBeautyTreatmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater,
            R.layout.fragment_beauty_treatment, container, false)
        return binding!!.root
    }

    companion object { @JvmStatic fun newInstance() = BeautyTreatmentFragment() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}
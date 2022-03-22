package kr.co.alphadopetshop.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import kr.co.alphadopetshop.BaseFragment
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.FragmentHealthCheckBinding


class HealthCheckFragment : BaseFragment() {
    private var binding: FragmentHealthCheckBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_health_check, container, false
        )
        return binding!!.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = HealthCheckFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}

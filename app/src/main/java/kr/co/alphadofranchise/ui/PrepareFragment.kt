package kr.co.alphadofranchise.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import kr.co.alphadofranchise.R
import kr.co.alphadofranchise.databinding.FragmentPrepareBinding
import kr.co.alphadofranchise.base.BaseFragment


class PrepareFragment : BaseFragment() {
    private var binding : FragmentPrepareBinding? = null
    private var position : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            position = it.getInt("position")
        }
    }

    companion object { @JvmStatic fun newInstance(position : Int) =
        PrepareFragment().apply {
            arguments = Bundle().apply { putInt("position", position) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater,
            R.layout.fragment_prepare, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
package kr.co.alphadofranchise.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import kr.co.alphadofranchise.R
import kr.co.alphadofranchise.databinding.FragmentMoreBinding
import kr.co.alphadofranchise.base.BaseFragment


class MoreFragment : BaseFragment() {
    private var binding : FragmentMoreBinding? = null

    companion object { @JvmStatic fun newInstance() = MoreFragment().apply { arguments = Bundle() } }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_more, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init(){
        binding!!.fragment = this
    }

    fun onItemClicked(){
        val builder = AlertDialog.Builder(context)
        builder.setMessage("서비스 준비중입니다.")
        builder.setPositiveButton("확인") { p0, p1 -> p0.dismiss() }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}
package kr.co.alphadopetshop.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.FragmentShoppingBinding


class ShoppingFragment : Fragment() {
    private var binding : FragmentShoppingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater,
            R.layout.fragment_shopping, container, false)
        return binding!!.root
    }

    companion object { @JvmStatic fun newInstance() = ShoppingFragment() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}
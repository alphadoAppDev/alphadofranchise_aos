package kr.co.alphadofranchise.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.alphadofranchise.R
import kr.co.alphadofranchise.databinding.ItemHeadOfficeInnerBinding
import kr.co.alphadofranchise.databinding.ItemHeadOfficeOutBinding
import kr.co.alphadofranchise.databinding.ItemRegionInnerBinding
import kr.co.alphadofranchise.databinding.ItemRegionOutBinding
import kr.co.alphadofranchise.models.HeadOfficeCell
import kr.co.alphadofranchise.models.HeadOfficeInnerCell
import kr.co.alphadofranchise.models.RegionCell
import kr.co.alphadofranchise.models.RegionInnerCell

class StoreOutAdapter(private val items : ArrayList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_REGION = 1
    private val TYPE_HEAD_OFFICE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_REGION) {
            val binding = DataBindingUtil.inflate<ItemRegionOutBinding>(
                LayoutInflater.from(parent.context), R.layout.item_region_out, parent, false
            )
            return RegionOutViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<ItemHeadOfficeOutBinding>(
                LayoutInflater.from(parent.context), R.layout.item_head_office_out, parent, false
            )
            return HeadOfficeOutViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(items[position] is RegionCell) {
            (holder as RegionOutViewHolder).bind(items[position] as RegionCell)
        } else {
            (holder as HeadOfficeOutViewHolder).bind(items[position] as HeadOfficeCell)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        if(items[position] is RegionCell) {
            return TYPE_REGION
        } else {
            return TYPE_HEAD_OFFICE
        }
    }

    class RegionOutViewHolder(private val binding : ItemRegionOutBinding): RecyclerView.ViewHolder(binding.root){
        private lateinit var adapter : StoreInnerAdapter
        private var isShow = false


        fun bind(item : RegionCell){
            binding.txtCell00.text = item.line0Array[0]
            binding.txtCell01.text = item.line0Array[1]
            binding.txtCell02.text = item.line0Array[2]
            binding.txtCell03.text = item.line0Array[3]
            binding.txtCell04.text = item.line0Array[4]
            binding.txtCell05.text = item.line0Array[5]
            binding.txtCell06.text = item.line0Array[6]

            binding.txtCell10.text = item.line1Array[0]
            binding.txtCell11.text = item.line1Array[1]
            binding.txtCell12.text = item.line1Array[2]
            binding.txtCell13.text = item.line1Array[3]
            binding.txtCell14.text = item.line1Array[4]
            binding.txtCell15.text = item.line1Array[5]
            binding.txtCell16.text = item.line1Array[6]


            if(item.innerRegionArray.size > 0) {
                binding.btnBottom.visibility = View.VISIBLE

                adapter = StoreInnerAdapter(item.innerRegionArray)
                binding.moreRecyclerView.adapter = adapter

                binding.btnBottom.setOnClickListener {
                    if(isShow) {
                        binding.moreRecyclerView.visibility = View.GONE
                        binding.btnBottom.setImageResource(R.drawable.icon_dropdown_gray)
                        isShow = false
                    } else {
                        binding.moreRecyclerView.visibility = View.VISIBLE
                        binding.btnBottom.setImageResource(R.drawable.icon_dropup_gray)
                        isShow = true
                    }
                }
            } else {
                binding.btnBottom.visibility = View.GONE
            }
        }
    }

    class HeadOfficeOutViewHolder(private val binding : ItemHeadOfficeOutBinding) : RecyclerView.ViewHolder(binding.root){
        private lateinit var adapter : StoreInnerAdapter
        private var isShow = false

        fun bind(item : HeadOfficeCell){
            binding.txtCell00.text = item.line0Array[0]
            binding.txtCell01.text = item.line0Array[1]
            binding.txtCell02.text = item.line0Array[2]
            binding.txtCell03.text = item.line0Array[3]
            binding.txtCell04.text = item.line0Array[4]

            binding.txtCell10.text = item.line1Array[0]
            binding.txtCell11.text = item.line1Array[1]
            binding.txtCell12.text = item.line1Array[2]
            binding.txtCell13.text = item.line1Array[3]
            binding.txtCell14.text = item.line1Array[4]



            if(item.innerHeadOfficeArray.size > 0) {
                binding.btnBottom.visibility = View.VISIBLE

                adapter = StoreInnerAdapter(item.innerHeadOfficeArray)
                binding.moreRecyclerView.adapter = adapter

                binding.btnBottom.setOnClickListener {
                    if(isShow) {
                        binding.moreRecyclerView.visibility = View.GONE
                        binding.btnBottom.setImageResource(R.drawable.icon_dropdown_gray)
                        isShow = false
                    } else {
                        binding.moreRecyclerView.visibility = View.VISIBLE
                        binding.btnBottom.setImageResource(R.drawable.icon_dropup_gray)
                        isShow = true
                    }
                }
            } else {
                binding.btnBottom.visibility = View.GONE
            }
        }
    }
}

class StoreInnerAdapter(private val items : ArrayList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val TYPE_INNTER_REGION = 3
    private val TYPE_INNTER_HEAD_OFFICE = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_INNTER_REGION) {
            val binding = DataBindingUtil.inflate<ItemRegionInnerBinding>(
                LayoutInflater.from(parent.context), R.layout.item_region_inner, parent, false
            )
            return RegionInnerViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<ItemHeadOfficeInnerBinding>(
                LayoutInflater.from(parent.context), R.layout.item_head_office_inner, parent, false
            )
            return HeadOfficeInnerViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(items[position] is RegionInnerCell) {
            (holder as RegionInnerViewHolder).bind(items[position] as RegionInnerCell)
        } else {
            (holder as HeadOfficeInnerViewHolder).bind(items[position] as HeadOfficeInnerCell)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        if(items[position] is RegionInnerCell) {
            return TYPE_INNTER_REGION
        } else {
            return TYPE_INNTER_HEAD_OFFICE
        }
    }

    class RegionInnerViewHolder(
        private val binding : ItemRegionInnerBinding
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item : RegionInnerCell){
            binding.txtCell00.text = item.innerRegionArray[0]
            binding.txtCell01.text = item.innerRegionArray[1]
            binding.txtCell02.text = item.innerRegionArray[2]
            binding.txtCell03.text = item.innerRegionArray[3]
            binding.txtCell04.text = item.innerRegionArray[4]
            binding.txtCell05.text = item.innerRegionArray[5]
            binding.txtCell06.text = item.innerRegionArray[6]
        }
    }

    class HeadOfficeInnerViewHolder(
        private val binding : ItemHeadOfficeInnerBinding
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item : HeadOfficeInnerCell){
            binding.txtCell00.text = item.innerHeadOfficeArray[0]
            binding.txtCell01.text = item.innerHeadOfficeArray[1]
            binding.txtCell02.text = item.innerHeadOfficeArray[2]
            binding.txtCell03.text = item.innerHeadOfficeArray[3]
            binding.txtCell04.text = item.innerHeadOfficeArray[4]
        }
    }
}
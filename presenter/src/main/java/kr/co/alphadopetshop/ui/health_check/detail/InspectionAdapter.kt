package kr.co.alphadopetshop.ui.health_check.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.ItemInspectionBinding
import kr.co.alphadopetshop.model.InspectionItem

class InspectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = ArrayList<InspectionItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemInspectionBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_inspection,
            parent,
            false
        )
        return InspectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as InspectionViewHolder).bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item : InspectionItem){
        items.add(item)
    }

    class InspectionViewHolder(private val binding : ItemInspectionBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item : InspectionItem){
            binding.txtOrder.text = String.format("%02d", item.order)
            binding.txtName.text = item.name
            binding.txtContent.text = item.content
        }
    }
}
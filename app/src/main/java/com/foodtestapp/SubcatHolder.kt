package com.foodtestapp

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodtestapp.databinding.ItemSubcategoryBinding

class SubcatHolder(
    itemView: View,
    listener: OnItemClickListener?,
    overlaySelectionListener: OnOverlaySelectionListener?
) : RecyclerView.ViewHolder(itemView), GenericAdapter.Binder<SubcatData> {

    var listener: OnItemClickListener? = null
    var overlaySelectionListener: OnOverlaySelectionListener? = null
    var binding: ItemSubcategoryBinding? = null

    init {
        binding = ItemSubcategoryBinding.bind(itemView)
        this.listener = listener
        this.overlaySelectionListener = overlaySelectionListener
    }


    override fun bind(data: SubcatData) {

        binding?.let { binding ->

            binding.cvSelectBackground.cardElevation = 0F

            Glide.with(BaseApplication.getContext())
                .load(data.icon)
                .into(binding.ivBackground)

            binding.tvBackgroundName.text = data.name

            if (data.isSelected) {
                binding.ivBorder.visibility = View.VISIBLE
                binding.tvBackgroundName.setTextColor(
                    ContextCompat.getColor(
                        BaseApplication.getContext(),
                        R.color.select_item
                    )
                )

            } else {
                binding.ivBorder.visibility = View.INVISIBLE
                binding.tvBackgroundName.setTextColor(
                    ContextCompat.getColor(
                        BaseApplication.getContext(),
                        R.color.black
                    )
                )
            }

            binding?.cvSelectBackground?.setOnClickListener {
                listener?.onItemClick(
                    it,
                    adapterPosition,
                    data
                )
            }
        }
    }
}
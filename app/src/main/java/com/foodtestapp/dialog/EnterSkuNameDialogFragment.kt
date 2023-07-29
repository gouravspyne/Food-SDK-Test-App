package com.foodtestapp.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.foodtestapp.HomeViewModel
import com.foodtestapp.R
import com.foodtestapp.base.BaseDialogFragment
import com.foodtestapp.databinding.DialogEnterSkuNameFragmentBinding

class EnterSkuNameDialogFragment : BaseDialogFragment<HomeViewModel, DialogEnterSkuNameFragmentBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//            binding.ivClose.setOnClickListener {
//                dismiss()
//            }

        binding.btConfirm.setOnClickListener {

            if (!binding.etSku.text.isNullOrEmpty()){
                viewModel.skuName = binding.etSku.text.toString()
                viewModel.startShoot.postValue(true)
                dismiss()
            }else if(binding.etSku.text.toString().contains("")){
                Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val padding = resources.getDimensionPixelOffset(R.dimen.gyro_reticle_stroke_width)
            val dialogWidth = resources.displayMetrics.widthPixels - 2 * padding

            // Set the gravity to BOTTOM
            dialog.window?.setGravity(Gravity.BOTTOM)

            // You can set a margin if required (adjust the margin value as needed)
            val marginBottom = resources.getDimensionPixelOffset(R.dimen.gyro_reticle_stroke_width)
            val layoutParams = dialog.window?.attributes
            layoutParams?.y = marginBottom

            dialog.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun getViewModel() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = DialogEnterSkuNameFragmentBinding.inflate(inflater, container, false)


}
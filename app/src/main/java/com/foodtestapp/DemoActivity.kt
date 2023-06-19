package com.foodtestapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.foodtestapp.databinding.ActivityDemoBinding
import com.spyneai.foodsdk.sdk.*

class DemoActivity : AppCompatActivity(), Spyne.SkuListener {

    private lateinit var binding: ActivityDemoBinding
    val TAG = "DemoActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContentView(binding.root)

        ArrayAdapter.createFromResource(
            this,
            R.array.subcategory_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spSubcategory.adapter = adapter
        }

        var skuId = intent.getStringExtra("sku_id")

        binding.etUserId.setText("userId")

        skuId?.let {
            binding.tvSkuId.text = it
        }


        binding.tvSkuId.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", binding.tvSkuId.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "SkuId copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        binding.btnStart.setOnClickListener {

            when {
                binding.etUserId.text.isNullOrEmpty() -> binding.etUserId.error = "Enter User Id"
                binding.etSku.text.isNullOrEmpty() -> binding.etSku.error = "Enter Sku Id"
                binding.etProjectName.text.isNullOrEmpty() -> binding.etSku.error =
                    "Enter project name"
                binding.etSkuName.text.isNullOrEmpty() -> binding.etSku.error = "Enter Sku name"
                else -> {
                    val builder = Spyne.ShootBuilder(
                        this,
                        binding.etUserId.text.toString(),
                        this,
                    )
                        .uniqueId(binding.etUserId.text.toString())
                        .projectName(binding.etProjectName.text.toString())
                        .skuName(binding.etSkuName.text.toString())
                        .metaData(HashMap<String, Any>().apply {
                            put("rid", "test rid")
                        })
                        .shootType(getShootType())
                        .classifier(getClassifierOptions())
                        .gyroMeter(getGyroMeterOptions())
                        .environment(if (AppConstants.BASE_URL == "https://beta-api.spyne.xyz/") "TESTING" else "PRODUCTION")
                        .shootLocation(binding.cbSku.isChecked)
                        .imageLocation(binding.cbImage.isChecked)

                    builder.subcategoryId(binding.spSubcategory.selectedItem.toString())

                    val spyne = builder.build()

                    spyne.start()
                }
            }
        }

    }

    private fun getGyroMeterOptions(): Gyrometer {
        return if (binding.gCbRestrictive.isChecked) Gyrometer.RESTRICTIVE
        else if (binding.gCbNonRestrictive.isChecked) Gyrometer.NON_RESTRICTIVE
        else Gyrometer.OFF
    }

    private fun getClassifierOptions(): Classifier {
        return if (binding.cCbRestrictive.isChecked) Classifier.RESTRICTIVE
        else if (binding.cCbNonRestrictive.isChecked) Classifier.NON_RESTRICTIVE
        else Classifier.OFF
    }

    private fun getShootType(): ShootType {
        return if (binding.cbUpload.isChecked) ShootType.UPLOAD
        else if (binding.cbShoot.isChecked) ShootType.SHOOT
        else ShootType.BOTH
    }

    override fun onShootCompleted(skuId: String, isReshoot: Boolean) {
    }

    override fun onClassificationFailed(retryCount: Int, data: String) {
        if (retryCount >= 3) {
            val builder = Spyne.ShootBuilder(
                this,
                binding.etUserId.text.toString(),
                this,
            ).classifier(Classifier.NON_RESTRICTIVE)

            val spyne = builder.build()
            spyne.unableUnrestrictiveFlow()
        }
    }


    override fun onSkuCreated(skuId: String) {
        binding.tvSkuId.text = skuId
    }


}
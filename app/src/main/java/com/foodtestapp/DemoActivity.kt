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
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
                binding.etProjectName.text.isNullOrEmpty() -> binding.etProjectName.error =
                    "Enter project name"
                binding.etSkuName.text.isNullOrEmpty() -> binding.etSkuName.error = "Enter Sku name"
                else -> {

                    // Create a shoot builder object
                    val builder = Spyne.ShootBuilder(
                        this,
                        binding.etUserId.text.toString(),
                        this,
                    )

                        // Configure Project and shoot Data
                        .uniqueId(binding.etSku.text.toString())
                        .projectName(binding.etProjectName.text.toString())
                        .skuName(binding.etSkuName.text.toString())
                        .metaData(HashMap<String, Any>().apply {
                            put("rid", "test rid")
                        })
                        .shootType(getShootType())
                        .maxNoOfUpload(binding.etNoOfImages.text.toString().toInt())
                        .classifier(getClassifierOptions())
                        .gyroMeter(getGyroMeterOptions())
                        .environment(if (AppConstants.BASE_URL == "https://beta-api.spyne.xyz/") "TESTING" else "PRODUCTION")

                    builder.subcategoryId(binding.spSubcategory.selectedItem.toString())

                    // start shoot
                    val spyne = builder.build()
                    spyne.start()
                }
            }
        }

        binding.btnReshoot.setOnClickListener {
            when {
                binding.etUserId.text.isNullOrEmpty() -> binding.etUserId.error = "Enter User Id"
                else -> {

                    // Create a re-shoot builder object
                    val builder = Spyne.ShootBuilder(
                        this,
                        binding.etUserId.text.toString(),
                        skuListener = this,
                    )
                        // Configure Project and re-shoot Data
                        .spyneSkuId(skuId ?: binding.etReshootSku.text.toString())
                        .gyroMeter(getGyroMeterOptions())
                        .gyroMeterVar(binding.etGyroDelta.text.toString().toInt())
                        .classifier(getClassifierOptions())
                        .imageLocation(true)


                    // start re-shoot
                    val spyne = builder.build()
                    spyne.reshoot()
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

    override fun onSkuCreated(skuId: String) {
        binding.tvSkuId.text = skuId
    }


}
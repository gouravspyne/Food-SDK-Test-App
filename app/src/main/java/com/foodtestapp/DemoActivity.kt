package com.foodtestapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import com.foodtestapp.databinding.ActivityDemoBinding
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.spyneai.foodsdk.sdk.Classifier
import com.spyneai.foodsdk.sdk.Gyrometer
import com.spyneai.foodsdk.sdk.ShootType
import com.spyneai.foodsdk.sdk.Spyne

//import com.spyneai.foodsdk.sdk.*

class DemoActivity : AppCompatActivity(), Spyne.SkuListener{

    private lateinit var binding: ActivityDemoBinding
    val TAG = "DemoActivity"
    private val MY_REQUEST_CODE: Int = 1
    lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)

        appUpdateManager = AppUpdateManagerFactory.create(this)

        checkUpdate()

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
                        .subcategoryId(binding.spSubcategory.selectedItem.toString())
                        .uniqueId(binding.etSku.text.toString())
                        .projectName(binding.etProjectName.text.toString())
                        .skuName(binding.etSkuName.text.toString())
                        .metaData(HashMap<String, Any>().apply {
                            put("rid", "test app rid")
                            put("user_id", "test app user")
                            put("item_id", "test app item id")
                            put("session_id", "test app session id")
                            put("source", "test app source")
                        })
                        .shootType(getShootType())
                        .minNoOfUpload(binding.etMinNoOfImages.text.toString().toInt())
                        .maxNoOfUpload(binding.etNoOfImages.text.toString().toInt())
                        .shootLocation(binding.cbSku.isChecked)
                        .imageLocation(binding.cbImage.isChecked)
                        .gyroMeter(getGyroMeterOptions())
                        .gyroMeterVar(binding.etGyroDelta.text.toString().toInt())
                        .classifier(getClassifierOptions())
                        .resumeDraft(binding.cbResumeDraft.isChecked)


                    val spyne = builder.build()
                    spyne.start()
                }
            }
        }

    }

    private fun checkUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    MY_REQUEST_CODE
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        MY_REQUEST_CODE
                    )
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                Toast.makeText(
                    this,
                    "Force update failed!, $requestCode",
                    Toast.LENGTH_SHORT
                ).show()
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


    override fun onSkuCreated(skuId: String) {
        binding.tvSkuId.text = skuId
    }
    override fun onShootCompleted(
        skuId: String, isReshoot: Boolean, imageCategory: String,
        outputImageUrl: String
    ) {

    }

    override fun onExitShoot(skuId: String, isDraftAvailable: Boolean) {
        Toast.makeText(this, "sku id${skuId}", Toast.LENGTH_SHORT).show()
    }

    override fun onClassificationFailed(retryCount: Int, data: String) {
        if (retryCount >= 1) {
            Spyne.enableUnrestrictiveFlow()
        }
    }



}
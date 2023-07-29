package com.foodtestapp

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.foodtestapp.databinding.ActivityHomeBinding
import com.foodtestapp.dialog.EnterSkuNameDialogFragment
import com.spyneai.foodsdk.sdk.ShootType
import com.spyneai.foodsdk.sdk.Spyne
import com.spyneai.foodsdk.swiggyshoot.viewpager.HorizontalMarginDecoration
import java.util.Random
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity(), OnOverlaySelectionListener, OnItemClickListener,
    Spyne.SkuListener {

    var bannerAdapter: BannerAdapter? = null
    var imageList = ArrayList<Drawable>()
    var subcatList = ArrayList<SubcatData>()
    private lateinit var binding: ActivityHomeBinding
    lateinit var viewModel: HomeViewModel
    var subcatAdapter: SubcatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        viewModel =
            ViewModelProvider(
                this,
                com.foodtestapp.ViewModelFactory()
            )[HomeViewModel::class.java]


        subcatList = ArrayList()

        subcatList.add(
            SubcatData(
                "Plate",
                ContextCompat.getDrawable(this, R.drawable.ic_plate)!!,
                "prod_foodplate",
                true
            )
        )
        subcatList.add(
            SubcatData(
                "Bowl",
                ContextCompat.getDrawable(this, R.drawable.ic_bowl)!!,
                "prod_foodbowl",
                false
            )
        )
        subcatList.add(
            SubcatData(
                "Pizza",
                ContextCompat.getDrawable(this, R.drawable.ic_pizza)!!,
                "prod_fodpizza",
                false
            )
        )
        subcatList.add(
            SubcatData(
                "Burger",
                ContextCompat.getDrawable(this, R.drawable.ic_burger)!!,
                "prod_foodburger",
                false
            )
        )
        subcatList.add(
            SubcatData(
                "Glass",
                ContextCompat.getDrawable(this, R.drawable.ic_glass)!!,
                "prod_foodglass",
                false
            )
        )
        subcatList.add(
            SubcatData(
                "Cake",
                ContextCompat.getDrawable(this, R.drawable.ic_cake)!!,
                "prod_foodcake",
                false
            )
        )
//        subcatList.add(
//            SubcatData(
//                "Others",
//                ContextCompat.getDrawable(this, R.drawable.ic_pizza)!!,
//                "prod_foodothers",
//                false
//            )
//        )

        subcatAdapter = SubcatAdapter(
            subcatList,
            this,
            this
        )

        binding.rvSubcategory.apply {
            layoutManager =
                GridLayoutManager(this@HomeActivity, 3, GridLayoutManager.VERTICAL, false)
            adapter = subcatAdapter
        }

        imageList = ArrayList()
        imageList.add(ContextCompat.getDrawable(this, R.drawable.ic_banner_1)!!)
        imageList.add(ContextCompat.getDrawable(this, R.drawable.ic_banner_2)!!)
        imageList.add(ContextCompat.getDrawable(this, R.drawable.ic_banner_3)!!)
        imageList.add(ContextCompat.getDrawable(this, R.drawable.ic_banner_4)!!)
        imageList.add(ContextCompat.getDrawable(this, R.drawable.ic_banner_5)!!)

        val adapter = BannerAdapter(
            this, imageList
        )

        binding.viewPagerBanner.adapter = adapter

        binding.viewPagerBanner.offscreenPageLimit = 2

        val nextItemVisiblePx =
            resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            // page.alpha = 0.25f + (1 - abs(position))
        }

        binding.viewPagerBanner.setPageTransformer(pageTransformer)

        val itemDecoration = HorizontalMarginDecoration(
            this, R.dimen.viewpager_current_item_horizontal_margin
        )
        binding.viewPagerBanner.addItemDecoration(itemDecoration)

        binding.viewPagerBanner.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                when (position) {
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("Selected_Page", position.toString())
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

        listners()

        startShoot()
    }

    private fun startShoot() {
        viewModel.startShoot.observe(this) {
            if (it) {
                viewModel.startShoot.postValue(false)
                val builder = Spyne.ShootBuilder(
                    this,
                    getUniqueIdentifier(),
                    this@HomeActivity,
                )
                    .subcategoryId(subcatList[0].id)
                    .uniqueId(getUniqueIdentifier())
                    .projectName(viewModel.skuName)
                    .skuName(viewModel.skuName)
                    .metaData(HashMap<String, Any>().apply {
                        put("rid", "test rid")
                    })
                    .shootType(getShootType())
                    .resumeDraft(false)


                val spyne = builder.build()
                spyne.start()
            }
        }
    }

    private fun getShootType(): ShootType {
        return if (viewModel.onStartShoot) {
            ShootType.SHOOT
        } else
            ShootType.UPLOAD
    }

    private fun getUniqueIdentifier(): String {
        val SALTCHARS = "abcdefghijklmnopqrstuvwxyz1234567890"
        val salt = StringBuilder()
        val rnd = Random()
        while (salt.length < 7) { // length of the random string.
            //val index = (rnd.nextFloat() * SALTCHARS.length) as Int
            val index = rnd.nextInt(SALTCHARS.length)
            salt.append(SALTCHARS[index])
        }
        return salt.toString()
    }

    fun listners() {

        binding.btShoot.setOnClickListener {
            viewModel.onStartShoot = true
            EnterSkuNameDialogFragment().show(
                this.supportFragmentManager,
                "EnterSkuNameDialogFragment"
            )
        }

        binding.btUpload.setOnClickListener {
            viewModel.onStartShoot = false
            EnterSkuNameDialogFragment().show(
                this.supportFragmentManager,
                "EnterSkuNameDialogFragment"
            )
        }

    }

    override fun onItemClick(view: View, position: Int, data: Any?) {
        if (data is SubcatData) {
            val selectedItem = subcatList.firstOrNull {
                it.isSelected
            }
            selectedItem?.isSelected = false
            data.isSelected = true
            subcatAdapter?.notifyItemChanged(subcatList.indexOf(selectedItem))
            subcatAdapter?.notifyItemChanged(position)
        }

    }

    override fun onSkuCreated(skuId: String) {
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

    override fun onOverlaySelected(view: View, position: Int, data: Any?) {

    }
}

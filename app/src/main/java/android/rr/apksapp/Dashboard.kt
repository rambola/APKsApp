package android.rr.apksapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.rr.apksapp.adapters.DashboardAdapter
import android.rr.apksapp.models.AppsApkDetails
import android.rr.apksapp.utils.AppConstants
import android.rr.apksapp.utils.ItemOffsetDecoration
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import java.io.*
import java.nio.channels.FileChannel
import java.util.*

class Dashboard : AppCompatActivity() {
    private val TAG = Dashboard::class.java.name
    private var mIsFabClicked = false
    private var mIsFabMenuShown = false
    private var mIsShowOrHideFab = false
    private var mInsideFolder: File? = null
    private val mArrayList: ArrayList<AppsApkDetails?>? = ArrayList()
    private val uris: ArrayList<Uri?>? = ArrayList()
    private lateinit var mBottomBtnLayout: LinearLayout
    private lateinit var mSaveAndShareBtn: Button
    private lateinit var mShareBtn: Button
    private lateinit var mSaveBtn: Button
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mRecyclerView: RecyclerView
    private var mDashboardAdapter: DashboardAdapter? = null
    private lateinit var mMainFab: FloatingActionButton
    private lateinit var mShareFab: FloatingActionButton
    private lateinit var mSaveFab: FloatingActionButton
    private lateinit var mExtractFab: FloatingActionButton
    private lateinit var mFabLayoutParams: FrameLayout.LayoutParams
    private var mFabRotateFwd: Animation? = null
    private var mFabRotateBwd: Animation? = null
    private var mShareFabShowAnim: Animation? = null
    private var mShareFabHideAnim: Animation? = null
    private var mSaveFabShowAnim: Animation? = null
    private var mSaveFabHideAnim: Animation? = null
    private var mExtractFabShowAnim: Animation? = null
    private var mExtractFabSHideAnim: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        mBottomBtnLayout = findViewById(R.id.bottomBtnLayout)
        mSaveAndShareBtn = findViewById(R.id.saveAndShareBtn)
        mShareBtn = findViewById(R.id.shareBtn)
        mSaveBtn = findViewById(R.id.saveBtn)
        mProgressBar = findViewById(R.id.progressBar)
        mRecyclerView = findViewById(R.id.dashboardRecyclerView)
        mMainFab = findViewById(R.id.main_fab)
        mShareFab = findViewById(R.id.fab_share)
        mSaveFab = findViewById(R.id.fab_save)
        mExtractFab = findViewById(R.id.fab_extract_apk)
        mRecyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this@Dashboard,
                AppConstants.GRID_COLUMNS)
        mRecyclerView.layoutManager =layoutManager
        val itemDecoration = ItemOffsetDecoration(this@Dashboard, R.dimen.item_offset)
        mRecyclerView.addItemDecoration(itemDecoration)

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkStoragePermission()) {
                getAppsApkDetails()
            } else {
                requestForStoragePermission()
            }
        } else {
            getAppsApkDetails()
        }

        mSaveAndShareBtn.setOnClickListener {
            Log.d(TAG, "selected save and share")
            saveAPKs()
            shareAPKs()
        }
        mShareBtn.setOnClickListener {
            Log.d(TAG, "selected share")
            shareAPKs()
        }
        mSaveBtn.setOnClickListener {
            Log.d(TAG, "selected save")
            saveAPKs()
        }
        mMainFab.setOnClickListener {
            if (!mIsFabClicked) {
                mIsFabClicked = true
                showMenuFab()
            } else {
                mIsFabClicked = false
                hideMenuFab()
            }
        }
        mShareFab.setOnClickListener { Log.e(TAG, "Share fab is clicked..") }
        mSaveFab.setOnClickListener { Log.e(TAG, "Save fab is clicked...") }
        mExtractFab.setOnClickListener { Log.e(TAG, "extract fab is clicked..") }

//        mShareFab.setOnClickListener(View.OnClickListener { Log.e(TAG, "Share fab is clicked..") })
//        mSaveFab.setOnClickListener(View.OnClickListener { Log.e(TAG, "Save fab is clicked...") })
//        mExtractFab.setOnClickListener(View.OnClickListener { Log.e(TAG, "extract fab is clicked..") })

        setupAnimations()
        /*private RippleDrawable rippleDrawable;
        private Button buttonWithRipple;

        buttonWithRipple = (Button) findViewById(R.id.buttonWithRipple);
        rippleDrawable = (RippleDrawable) buttonWithRipple.getBackground();
        buttonWithRipple.setOnTouchListener(this);

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.buttonWithRipple :
                    rippleDrawable.setHotspot(event.getX(), event.getY());
                    rippleDrawable.setColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    break; }
            return false;
        }*/
    }

    private fun getAppsApkDetails() {
//        Thread {
            mProgressBar.visibility = View.VISIBLE
            var appName: String
            var apkFile: File
            var drawable: Drawable?
            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            val apps = packageManager.queryIntentActivities(mainIntent, 0)
            for (info in apps) {
                try {
                    val applicationInfo = this@Dashboard.packageManager.getApplicationInfo(
                            info.activityInfo.applicationInfo.packageName, 0)
                    appName = packageManager.getApplicationLabel(applicationInfo) as String
                    drawable = packageManager.getApplicationIcon(applicationInfo)
                    apkFile = File(applicationInfo.publicSourceDir)
                    mArrayList?.add(AppsApkDetails(appName,
                            apkFile.absolutePath, drawable, false))
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                } finally {
                    val appsApkDetailsComparator = Comparator<AppsApkDetails?> {
                        appsApkDetails1,
                        appsApkDetails2 -> appsApkDetails1.appName!!.compareTo(
                            appsApkDetails2.appName.toString(), ignoreCase = true)
                    }

                    Collections.sort(mArrayList, appsApkDetailsComparator)
                    runOnUiThread {
                        this.mDashboardAdapter?.let {
                            mDashboardAdapter!!.updateAdapter(this@Dashboard, mArrayList)
                        } ?: kotlin.run {
                            this.mDashboardAdapter = DashboardAdapter(
                                    this@Dashboard, mArrayList)
                            mRecyclerView.adapter = mDashboardAdapter
                            mRecyclerView.adapter = mDashboardAdapter
                        }
//                        if (null == mDashboardAdapter) {
//                            mDashboardAdapter = DashboardAdapter(
//                                    this@Dashboard, mArrayList)
//                            mRecyclerView.adapter = mDashboardAdapter
//                            mRecyclerView.adapter = mDashboardAdapter
//                        } else {
//                            mDashboardAdapter.updateAdapter(this@Dashboard, mArrayList)
//                        }
                        mProgressBar.visibility = View.GONE
                    }
                }
            }
//        }.start()
    }

    fun showOrHideBottomBar(showOrHide: Boolean) {
        mIsShowOrHideFab = showOrHide
        if (showOrHide) {
//            mBottomBtnLayout.setVisibility(View.VISIBLE);
            mMainFab.clearAnimation()
            mMainFab.show()
        } else {
//            mBottomBtnLayout.setVisibility(View.GONE);
            if (mIsFabMenuShown) hideMenuFab() else mMainFab.hide()
            mIsFabClicked = false
            mIsFabMenuShown = false
        }

//        if (null != mBottomBtnLayout && showOrHide) {
////            mBottomBtnLayout.setVisibility(View.VISIBLE);
//            mMainFab.clearAnimation()
//            mMainFab.show()
//        } else if (null != mBottomBtnLayout && !showOrHide) {
////            mBottomBtnLayout.setVisibility(View.GONE);
//            if (mIsFabMenuShown) hideMenuFab() else mMainFab.hide()
//            mIsFabClicked = false
//            mIsFabMenuShown = false
//        }
    }

    private fun saveAPKs() {
        if (checkStoragePermission()) {
            createFolders()
        } else {
            requestForStoragePermission()
        }
    }

    private fun shareAPKs() {
        Log.d(TAG, "shareAPKs()")
        Thread {
            val arrayList = mDashboardAdapter?.getTheSelectedAppsList()
            Log.d(TAG, "shareAPKs(), arrayList.size(): " +
                    (arrayList?.size ?: -1))
            if (getSelectedItemsCount(arrayList) > 0) {
                uris?.clear()
                for (i in arrayList?.indices!!) {
                    Log.d(TAG, "shareAPKs(), getApkFilePath(): " +
                            arrayList[i]!!.apkFilePath)
                    uris!!.add(Uri.fromFile(File(arrayList[i]!!.apkFilePath)))
                }
            }
            Log.d(TAG, "shareAPKs(), uris.size(): " + uris!!.size)
            runOnUiThread {
                if (uris.size > 0) {
                    val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                    intent.type = "application/vnd.android.package-archive"
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                            uris)
                    startActivity(Intent.createChooser(intent, "Share " +
                            uris.size + " Files Via"))
                }
            }
        }.start()
    }

    private fun createFolders() {
        val arrayList = mDashboardAdapter?.getTheSelectedAppsList()
        Log.d(TAG, "saveAPKs(), arrayList.size(): " +
                (arrayList?.size ?: -1))
        if (getSelectedItemsCount(arrayList) > 0) {
            val appFolder = File(AppConstants.FOLDER_PATH + File.separator +
                    AppConstants.FOLDER_NAME)
            mInsideFolder = File(AppConstants.FOLDER_PATH + File.separator +
                    AppConstants.FOLDER_NAME + File.separator +
                    AppConstants.CURRENT_INSIDE_FOLDER_NAME)
            if (!appFolder.exists()) {
                appFolder.mkdirs()
            }
            if (!mInsideFolder!!.exists()) {
                mInsideFolder!!.mkdirs()
            }
            copyAPKsToCurrentFolder(mInsideFolder!!.getAbsolutePath(), arrayList)
        }
    }

    private fun copyAPKsToCurrentFolder(insideFolderPath: String?,
                                        arrayList: ArrayList<AppsApkDetails?>?) {
        Log.d(TAG, "copyAPKsToCurrentFolder(), insideFolderPath: $insideFolderPath")
        Thread {
            var sourceChannel: FileChannel? = null
            var destinationChannel: FileChannel? = null
            for (i in arrayList!!.indices) {
                try {
                    val sourceFile = File(arrayList[i]!!.apkFilePath)
                    val destinationFile = File(insideFolderPath + File.separator +
                            sourceFile.name)
                    if (!checkWhetherAPKIsAlreadySaved(destinationFile.absolutePath)) {
                        sourceChannel = FileInputStream(sourceFile).channel
                        destinationChannel = FileOutputStream(destinationFile).channel
                        destinationChannel.transferFrom(sourceChannel, 0,
                                sourceChannel.size())
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    if (sourceChannel != null) {
                        try {
                            sourceChannel.run { close() }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    if (destinationChannel != null) {
                        try {
                            destinationChannel.run { close() }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            runOnUiThread { Toast.makeText(this@Dashboard, "APKs saved", Toast.LENGTH_SHORT).show() }
        }.start()
    }

    private fun checkWhetherAPKIsAlreadySaved(apkPath: String?): Boolean {
        val files = mInsideFolder!!.listFiles()
        var alreadySaved = false
        for (file in files) {
            if (file.absolutePath.equals(apkPath, ignoreCase = true)) {
                alreadySaved = true
                break
            }
        }
        Log.d(TAG, "checkWhetherAPKIsAlreadySaved(), alreadySaved: $alreadySaved")
        return alreadySaved
    }

    private fun getSelectedItemsCount(appsApkDetails: ArrayList<AppsApkDetails?>?): Int {
        var selectedItemsCount = 0
        for (i in appsApkDetails!!.indices) {
            if (appsApkDetails[i]!!.isSelected) {
                selectedItemsCount++
            }
        }
        Log.d(TAG, "getSelectedItemsCount(), selectedItemsCount: $selectedItemsCount")
        return selectedItemsCount
    }

    private fun checkStoragePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this@Dashboard,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestForStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@Dashboard, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this@Dashboard, getString(R.string.storagePermissionToast),
                    Toast.LENGTH_LONG).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(this@Dashboard,
                        arrayOf<String?>(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE),
                        AppConstants.REQUEST_CODE_STORAGE_PERMISSION)
            }
//            ActivityCompat.requestPermissions(this@Dashboard,
//                    arrayOf<String?>(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                            Manifest.permission.READ_EXTERNAL_STORAGE),
//                    AppConstants.REQUEST_CODE_STORAGE_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.REQUEST_CODE_STORAGE_PERMISSION -> {
                Log.d(TAG, "grantResults.length: " + grantResults.size)
                if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mBottomBtnLayout.visibility == View.VISIBLE) {
                        createFolders()
                    }
                } else {
                    Toast.makeText(this@Dashboard,
                            getString(R.string.storagePermissionToast),
                            Toast.LENGTH_LONG).show()
                }
                if (mBottomBtnLayout.visibility == View.GONE) {
                    getAppsApkDetails()
                }
            }
        }
    }

    //Animations
    private fun setupAnimations() {
        mFabRotateFwd = AnimationUtils.loadAnimation(application, R.anim.rotate_forward)
        mFabRotateBwd = AnimationUtils.loadAnimation(application, R.anim.rotate_backward)
        mShareFabShowAnim = AnimationUtils.loadAnimation(application, R.anim.share_fab_show)
        mShareFabHideAnim = AnimationUtils.loadAnimation(application, R.anim.share_fab_hide)
        mSaveFabShowAnim = AnimationUtils.loadAnimation(application, R.anim.save_fab_show)
        mSaveFabHideAnim = AnimationUtils.loadAnimation(application, R.anim.save_fab_hide)
        mExtractFabShowAnim = AnimationUtils.loadAnimation(application, R.anim.extract_fab_show)
        mExtractFabSHideAnim = AnimationUtils.loadAnimation(application, R.anim.extract_fab_hide)
    }

    private fun showMenuFab() {
        mIsFabMenuShown = true
        rotateFabForward()
        mShareFab.isClickable = true
        mFabLayoutParams = mShareFab.layoutParams as FrameLayout.LayoutParams
        mFabLayoutParams.rightMargin += (mShareFab.width * 1.7).toInt()
        mFabLayoutParams.bottomMargin += (mShareFab.height * 0.25).toInt()
        mShareFab.layoutParams = mFabLayoutParams
        mShareFab.startAnimation(mShareFabShowAnim)
        mSaveFab.isClickable = true
        mFabLayoutParams = mSaveFab.layoutParams as FrameLayout.LayoutParams
        mFabLayoutParams.rightMargin += (mSaveFab.width * 1.5).toInt()
        mFabLayoutParams.bottomMargin += (mSaveFab.height * 1.5).toInt()
        mSaveFab.layoutParams = mFabLayoutParams
        mSaveFab.startAnimation(mSaveFabShowAnim)
        mExtractFab.isClickable = true
        mFabLayoutParams = mExtractFab.layoutParams as FrameLayout.LayoutParams
        mFabLayoutParams.rightMargin += (mExtractFab.width * 0.25).toInt()
        mFabLayoutParams.bottomMargin += (mExtractFab.height * 1.7).toInt()
        mExtractFab.layoutParams = mFabLayoutParams
        mExtractFab.startAnimation(mExtractFabShowAnim)
    }

    private fun hideMenuFab() {
        mFabLayoutParams = mShareFab.layoutParams as FrameLayout.LayoutParams
        mFabLayoutParams.rightMargin -= (mShareFab.width * 1.7).toInt()
        mFabLayoutParams.bottomMargin -= (mShareFab.height * 0.25).toInt()
        mShareFab.layoutParams = mFabLayoutParams
        mShareFab.startAnimation(mShareFabHideAnim)
        mShareFab.isClickable = false
        mFabLayoutParams = mSaveFab.layoutParams as FrameLayout.LayoutParams
        mFabLayoutParams.rightMargin -= (mSaveFab.width * 1.5).toInt()
        mFabLayoutParams.bottomMargin -= (mSaveFab.height * 1.5).toInt()
        mSaveFab.layoutParams = mFabLayoutParams
        mSaveFab.startAnimation(mSaveFabHideAnim)
        mSaveFab.isClickable = false
        mFabLayoutParams = mExtractFab.layoutParams as FrameLayout.LayoutParams
        mFabLayoutParams.rightMargin -= (mExtractFab.width * 0.25).toInt()
        mFabLayoutParams.bottomMargin -= (mExtractFab.height * 1.7).toInt()
        mExtractFab.layoutParams = mFabLayoutParams
        mExtractFab.startAnimation(mExtractFabSHideAnim)
        mExtractFab.isClickable = false
        mIsFabMenuShown = false
        rotateFabBackward()
    }

    private fun rotateFabForward() {
        /*ViewCompat.animate(mMainFab)
                .rotation(135.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(10.0F))
                .start();*/
        mShareFab.clearAnimation()
        mSaveFab.clearAnimation()
        mExtractFab.clearAnimation()
        mMainFab.clearAnimation()
        mMainFab.startAnimation(mFabRotateFwd)
    }

    private fun rotateFabBackward() {
        /*ViewCompat.animate(mMainFab)
                .rotation(0.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(10.0F))
                .start();

        hideMenuFab();*/
//       mShareFab.clearAnimation();
//       mSaveFab.clearAnimation();
//       mExtractFab.clearAnimation();
        mMainFab.startAnimation(mFabRotateBwd)
        //mMainFab.clearAnimation();
        if (!mIsShowOrHideFab) mMainFab.hide()
    }

}
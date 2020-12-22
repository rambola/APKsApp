package android.rr.apksapp.models

import android.graphics.drawable.Drawable

data class AppsApkDetails(val appName: String?, val apkFilePath: String?,
                          val drawable: Drawable?, var isSelected: Boolean) //{
    /*@JvmName("getAppName1")
    fun getAppName(): String? {
        return appName
    }

    @JvmName("getApkFilePath1")
    fun getApkFilePath(): String? {
        return apkFilePath
    }

    fun getDrawable(): Drawable? {
        return drawable
    }

    fun setIsSelected(isSelected: Boolean) {
        this.isSelected = isSelected
    }

    fun getIsSelected(): Boolean {
        return isSelected
    }*/
//}
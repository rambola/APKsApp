package android.rr.apksapp.utils

import android.os.Environment
import java.text.SimpleDateFormat
import java.util.*

object AppConstants {
    const val GRID_COLUMNS = 3
    const val REQUEST_CODE_STORAGE_PERMISSION = 1234
    val FOLDER_NAME: String? = "AppsAPK"
    val FOLDER_PATH = Environment.getExternalStorageDirectory().absolutePath
    var CURRENT_INSIDE_FOLDER_NAME = getTodayDateWithCurrentTime()
    private val SIMPLE_DATE_FORMAT: String? = "dd-MM-yyyy"
    private fun getTodayDateWithCurrentTime(): String? {
        val simpleDateFormat = SimpleDateFormat(SIMPLE_DATE_FORMAT)
        return simpleDateFormat.format(Date(System.currentTimeMillis()))
    }
}
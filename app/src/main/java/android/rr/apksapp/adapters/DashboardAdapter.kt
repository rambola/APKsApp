package android.rr.apksapp.adapters

import android.content.Context
import android.rr.apksapp.Dashboard
import android.rr.apksapp.R
import android.rr.apksapp.models.AppsApkDetails
import android.rr.apksapp.utils.GlideCircleTransform
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.util.*

class DashboardAdapter(private var context: Context?, private var appsApkDetails: ArrayList<AppsApkDetails?>?) : RecyclerView.Adapter<DashboardAdapter.ViewHolder?>() {
    private var isLongClicked = false
    private val TAG = DashboardAdapter::class.java.simpleName

    fun updateAdapter(context: Context?, appsApkDetails: ArrayList<AppsApkDetails?>?) {
        this.context = context
        this.appsApkDetails = appsApkDetails
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        //TODO("Not yet implemented")
        return ViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.for_dashboard_grid, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        //TODO("Not yet implemented")
        viewHolder.bindData(position)
    }

    override fun getItemCount(): Int {
        return appsApkDetails?.size!!
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!),
            View.OnClickListener, OnLongClickListener {
        var appNameTV: TextView? = itemView?.findViewById(R.id.appNameTV)
        var appIconIV: ImageView? = itemView?.findViewById(R.id.appIconIV)
        var clickedPosition = 0

        fun bindData(position: Int) {
            clickedPosition = position
            Glide.with(context).load("").placeholder(appsApkDetails?.get(position)
                    ?.drawable).transform(GlideCircleTransform(context)).into(appIconIV)
            appNameTV?.text = appsApkDetails?.get(position)?.appName
            if (appsApkDetails?.get(position)?.isSelected!!) {
                context?.resources?.getColor(R.color.cardSelected)?.let { itemView.setBackgroundColor(it) }
            } else {
                context?.resources?.getColor(R.color.cardUnSelected)?.let { itemView.setBackgroundColor(it) }
            }
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(view: View?) {
            Log.d(TAG, "onClick(), clickedPosition: " + clickedPosition +
                    ", isLongClicked: " + isLongClicked)
            if (isLongClicked) {
                if (appsApkDetails?.get(clickedPosition)?.isSelected!!) {
                    appsApkDetails!![clickedPosition]?.isSelected =  false
                    context?.resources?.getColor(R.color.cardUnSelected)?.let { itemView.setBackgroundColor(it) }
                } else {
                    appsApkDetails?.get(clickedPosition)?.isSelected = true
                    context?.resources?.getColor(R.color.cardSelected)?.let { itemView.setBackgroundColor(it) }
                }
            }
            if (0 == getSelectedItemsCount()) {
                isLongClicked = false
                (context as Dashboard?)?.showOrHideBottomBar(false)
            }
        }

        override fun onLongClick(view: View?): Boolean {
            Log.d(TAG, "onLongClick(), clickedPosition: " + clickedPosition +
                    ", isLongClicked: " + isLongClicked)
            if (!isLongClicked) {
                isLongClicked = true
                (context as Dashboard?)?.showOrHideBottomBar(true)
                appsApkDetails?.get(clickedPosition)?.isSelected = true
                context?.resources?.getColor(R.color.cardSelected)?.let { itemView.setBackgroundColor(it) }
//                itemView.setBackgroundColor(context?.resources?.getColor(R.color.cardSelected))
            }
            return true
        }

    }

    private fun getSelectedItemsCount(): Int {
        var selectedItemsCount = 0
        for (i in appsApkDetails?.indices!!) {
            if (appsApkDetails!![i]?.isSelected!!) {
                selectedItemsCount++
            }
        }

        Log.d(TAG, "getSelectedItemsCount(), selectedItemsCount: $selectedItemsCount")
        return selectedItemsCount
    }

    fun getTheSelectedAppsList(): ArrayList<AppsApkDetails?>? {
        val arrayList = ArrayList<AppsApkDetails?>()
        appsApkDetails?.let { arrayList.addAll(it) }
//        arrayList.addAll(appsApkDetails)
        var i = 0
        while (i < arrayList.size) {
            if (!arrayList[i]?.isSelected!!) {
                arrayList.removeAt(i)
                i--
            }
            i++
        }
        return arrayList
    }

}
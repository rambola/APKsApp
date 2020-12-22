//package android.rr.apksapp.utils
//
//import android.content.Context
//import android.support.v7.widget.RecyclerView
//import android.support.v7.widget.RecyclerView.OnItemTouchListener
//import android.view.GestureDetector
//import android.view.GestureDetector.SimpleOnGestureListener
//import android.view.MotionEvent
//
//class RecyclerViewTouchListener(context: Context?, recyclerView: RecyclerView?, private val clickListener: ClickListener?) : OnItemTouchListener {
//    private val gestureDetector: GestureDetector?
//    override fun onInterceptTouchEvent(rv: RecyclerView?, e: MotionEvent?): Boolean {
//        val child = rv.findChildViewUnder(e.getX(), e.getY())
//        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
//            clickListener.onClick(child, rv.getChildPosition(child))
//        }
//        return false
//    }
//
//    override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {}
//    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
//
//    init {
//        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
//            override fun onSingleTapUp(e: MotionEvent?): Boolean {
//                return true
//            }
//
//            override fun onLongPress(e: MotionEvent?) {
//                val child = recyclerView.findChildViewUnder(e.getX(), e.getY())
//                if (child != null && clickListener != null) {
//                    clickListener.onLongClick(child, recyclerView.getChildPosition(child))
//                }
//            }
//        })
//    }
//}
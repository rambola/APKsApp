//package android.rr.apksapp.utils
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Rect
//import android.graphics.drawable.Drawable
//import android.support.v7.widget.LinearLayoutManager
//import android.support.v7.widget.RecyclerView
//import android.support.v7.widget.RecyclerView.ItemDecoration
//import android.view.View
//
//class RecyclerViewItemDivider(context: Context?, orientation: Int, itemDividerSize: Int) : ItemDecoration() {
//    private val mDivider: Drawable?
//    private var mOrientation = 0
//    private val itemDividerSize: Int
//    fun setOrientation(orientation: Int) {
//        require(!(orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST)) { "invalid orientation" }
//        mOrientation = orientation
//    }
//
//    override fun onDrawOver(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
//        if (mOrientation == VERTICAL_LIST) {
//            drawVertical(c, parent)
//        } else {
//            drawHorizontal(c, parent)
//        }
//    }
//
//    fun drawVertical(c: Canvas?, parent: RecyclerView?) {
//        val left = parent.getPaddingLeft()
//        val right = parent.getWidth() - parent.getPaddingRight()
//        val childCount = parent.getChildCount()
//        for (i in 0 until childCount) {
//            val child = parent.getChildAt(i)
//            val params = child
//                    .layoutParams as RecyclerView.LayoutParams
//            val top = child.bottom + params.bottomMargin
//            val bottom = top + mDivider.getIntrinsicHeight()
//            mDivider.setBounds(left, top, right, bottom)
//            mDivider.draw(c)
//        }
//    }
//
//    fun drawHorizontal(c: Canvas?, parent: RecyclerView?) {
//        val top = parent.getPaddingTop()
//        val bottom = parent.getHeight() - parent.getPaddingBottom()
//        val childCount = parent.getChildCount()
//        for (i in 0 until childCount) {
//            val child = parent.getChildAt(i)
//            val params = child
//                    .layoutParams as RecyclerView.LayoutParams
//            val left = child.right + params.rightMargin
//            val right = left + mDivider.getIntrinsicHeight()
//            mDivider.setBounds(left, top, right, bottom)
//            mDivider.draw(c)
//        }
//    }
//
//    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
//        if (mOrientation == VERTICAL_LIST) {
//            outRect.set(0, 0, 0, itemDividerSize)
//        } else {
//            outRect.set(0, 0, itemDividerSize, 0)
//        }
//    }
//
//    companion object {
//        private val ATTRS: IntArray? = intArrayOf(
//                android.R.attr.listDivider
//        )
//        const val HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL
//        const val VERTICAL_LIST = LinearLayoutManager.VERTICAL
//    }
//
//    init {
//        val a = context.obtainStyledAttributes(ATTRS)
//        mDivider = a.getDrawable(0)
//        a.recycle()
//        setOrientation(orientation)
//        this.itemDividerSize = itemDividerSize
//    }
//}
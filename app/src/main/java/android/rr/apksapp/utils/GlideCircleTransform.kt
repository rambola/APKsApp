package android.rr.apksapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

class GlideCircleTransform(context: Context?) : BitmapTransformation(context) {
    override fun transform(pool: BitmapPool?, toTransform: Bitmap?,
                           outWidth: Int, outHeight: Int): Bitmap? {
        return circleCrop(pool, toTransform)
    }

    override fun getId(): String? {
        return javaClass.name
    }

    companion object {
        private fun circleCrop(pool: BitmapPool?, source: Bitmap?): Bitmap? {
            if (source == null) return null
            val size = source.width.coerceAtMost(source.height)
            val x = (source.width - size) / 2
            val y = (source.height - size) / 2

            // TODO this could be acquired from the pool too
            val squared = Bitmap.createBitmap(source, x, y, size, size)
            var result = pool?.get(size, size, Bitmap.Config.ARGB_8888)
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            }
            val canvas = Canvas(result)
            val paint = Paint()
//            paint.shader = BitmapShader(squared, BitmapShader.TileMode.CLAMP,
//                    BitmapShader.TileMode.CLAMP)
            paint.isAntiAlias = true
            val r = size / 2f
            canvas.drawCircle(r, r, r, paint)
            return result
        }
    }
}
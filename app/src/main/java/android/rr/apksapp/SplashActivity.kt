package android.rr.apksapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    var handler: Handler? = null
    var runnable: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        handler = Handler()
        runnable = Runnable {
            startActivity(Intent(this@SplashActivity, Dashboard::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        handler?.postDelayed(runnable, 1500)
    }

    override fun onStop() {
        super.onStop()
        handler?.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != handler) handler = null
        if (null != runnable) runnable = null
    }
}
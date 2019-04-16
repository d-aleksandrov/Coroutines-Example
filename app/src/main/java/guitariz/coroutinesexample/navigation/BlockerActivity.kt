package guitariz.coroutinesexample.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import guitariz.coroutinesexample.R
import org.jetbrains.anko.runOnUiThread

interface Blocker {
    fun close(closeCallback: () -> Unit)
}

class BlockerActivity : AppCompatActivity(), Blocker {
    companion object {
        private var callback: ((Blocker) -> Unit)? = null
        private var instance: Blocker? = null
        private var closeCallback: (() -> Unit)? = null
        fun show(context: Context, callback: (Blocker?) -> Unit) {
            if (callback != null)
                return callback(null)
            Companion.callback = callback
            context.runOnUiThread {
                val intent = Intent(context, BlockerActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocker)
    }

    override fun onResume() {
        super.onResume()
        instance = this
        callback?.let { cb ->
            callback = null
            cb(this)
        }
    }

    override fun close(closeCallback: () -> Unit) {
        if (instance == null)
            return closeCallback()
        runOnUiThread {
            instance = null
            Companion.closeCallback = closeCallback
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closeCallback?.invoke()
        closeCallback = null
    }
}
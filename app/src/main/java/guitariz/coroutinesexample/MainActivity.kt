package guitariz.coroutinesexample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import guitariz.coroutinesexample.navigation.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity(), Navigation {
    override fun showBlocker(callback: (Blocker?) -> Unit) = BlockerActivity.show(this, callback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainButtonAsync.setOnClickListener { _ ->
            var time = System.currentTimeMillis()
            asyncMethod {
                time = System.currentTimeMillis() - time;
                Log.d("ASYNC_TEST", (time).toString())
            }
        }

        mainButtonCoroutine.setOnClickListener { _ ->
            var time = System.currentTimeMillis()
            coroutineMethod {
                time = System.currentTimeMillis() - time;
                Log.d("COROUTINE_TEST", (time).toString())
            }
        }
    }

    fun someWork(callback: () -> Unit) {
        doAsync { callback() }
    }

    fun asyncMethod(result: () -> Unit) {
        doAsync {
            showBlocker { blocker ->
                someWork {
                    blocker?.close {
                        doAsync { result() }
                    } ?: doAsync { result() }
                }
            }
        }
    }

    fun coroutineMethod(result: () -> Unit) = GlobalScope.launch {
        val blocker = showBlockerAsync()
        someWorkAsync()
        blocker?.closeAsync()
        result()
    }

    suspend fun someWorkAsync(): Unit = suspendCoroutine { someWork { it.resume(Unit) } }
}
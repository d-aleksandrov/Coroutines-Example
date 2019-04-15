package guitariz.coroutinesexample.modules

import guitariz.coroutinesexample.enqueue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface Navigation {
    fun showBlocker(callback: () -> Unit)
    fun hideBlocker(callback: () -> Unit)
}

fun SimpleUseCase(navigation: Navigation,
                  network: NewsNetwork,
                  fileUrl:String){
    navigation.showBlocker {
        network.downloadFile(fileUrl).enqueue { body, throwable ->
            navigation.hideBlocker {
                processResults(body, throwable?.message)
            }
        }
    }
}

suspend fun Navigation.showBlockerAsync()
        : Unit = suspendCoroutine { showBlocker { it.resume(Unit) } }

suspend fun Navigation.hideBlockerAsync()
        : Unit = suspendCoroutine { hideBlocker { it.resume(Unit) } }

fun SimpleUseCaseAsync(navigation: Navigation,
                       network: NewsNetwork,
                       fileUrl:String) = GlobalScope.launch {
    navigation.showBlockerAsync()
    val response = network.downloadFileAsync(fileUrl).await()
    navigation.hideBlockerAsync()
    processResults(response.body(), response.errorBody()?.string())
}

fun processResults(responseBody: ResponseBody?, message: String?) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

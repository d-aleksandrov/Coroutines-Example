package guitariz.coroutinesexample.navigation

import guitariz.coroutinesexample.enqueue
import guitariz.coroutinesexample.modules.NewsNetwork
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface Navigation {
    fun showBlocker(callback: (Blocker?) -> Unit)
}

fun SimpleUseCase(navigation: Navigation,
                  network: NewsNetwork,
                  fileUrl: String) {
    navigation.showBlocker { blocker ->
        network.downloadFile(fileUrl).enqueue { body, throwable ->
            blocker?.close {
                processResults(body, throwable?.message)
            } ?: processResults(body, throwable?.message)
        }
    }
}

suspend fun Navigation.showBlockerAsync(): Blocker? = suspendCoroutine { showBlocker(it::resume) }

suspend fun Blocker.closeAsync(): Unit = suspendCoroutine { close { it.resume(Unit) } }

fun SimpleUseCaseAsync(navigation: Navigation,
                       network: NewsNetwork,
                       fileUrl: String) = GlobalScope.launch {
    val blocker = navigation.showBlockerAsync()
    val response = network.downloadFileAsync(fileUrl).await()
    blocker?.closeAsync()
    processResults(response.body(), response.errorBody()?.string())
}

fun processResults(responseBody: ResponseBody?, message: String?) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

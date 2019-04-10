package guitariz.coroutinesexample

import guitariz.coroutinesexample.modules.FileStorageService
import guitariz.coroutinesexample.modules.NewsDao
import guitariz.coroutinesexample.modules.NewsNetwork
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface NewsCacheListener {
    fun onSuccess()
    fun onError(error: Throwable)
    fun onProgress(percent: Int)
}

class NewsRepository {
    private val network:NewsNetwork = TODO()
    private val database:NewsDao = TODO()
    private val fileStorage:FileStorageService = TODO()

    fun cacheNews(listener: NewsCacheListener) = FileCacheWorker(
            network = network,
            database = database,
            fileStorage = fileStorage,
            listener = listener
    )

    fun cacheNewsCoroutine(listener: NewsCacheListener) = GlobalScope.launch {
        val lastID = database.lastElement().firstOrNull()?.id
                ?: return@launch listener.onError(Throwable("do not have last news id"))

        val loadedNews = network.loadNewsDeferred(lastID)
        if loadedNews.
    }
}
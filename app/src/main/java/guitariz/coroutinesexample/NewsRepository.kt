package guitariz.coroutinesexample

import guitariz.coroutinesexample.modules.FileStorageService
import guitariz.coroutinesexample.modules.NewsDao
import guitariz.coroutinesexample.modules.NewsNetwork
import guitariz.coroutinesexample.modules.storeAsync
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface NewsCacheListener {
    fun onSuccess(techMessage:String?)
    fun onError(error: Throwable)
    fun onProgress(percent: Int)
}

class NewsRepository {
    private val network: NewsNetwork = TODO()
    private val database: NewsDao = TODO()
    private val fileStorage: FileStorageService = TODO()

    fun cacheNews(listener: NewsCacheListener) = FileCacheWorker(
            network = network,
            database = database,
            fileStorage = fileStorage,
            listener = listener
    )

    fun cacheNewsCoroutine(listener: NewsCacheListener) = GlobalScope.launch {
        val lastID = database.lastElement().firstOrNull()?.id
                ?: return@launch listener.onError(Throwable("do not have last news id"))

        val loadedNews = network.loadNewsAsync(lastID).await()
        val newsArray = loadedNews.body()

        if (newsArray == null) {
            val message = loadedNews.errorBody()?.string() ?: "Bad load results "
            return@launch listener.onError(Throwable(message))
        }

        val fileUrls = newsArray.mapNotNull(NewsModel::attachments).flatten()
        val fileErrorStrings:List<String> = fileUrls.mapNotNull { fileUrl ->
            val fileResponse = network.downloadFileAsync(fileUrl).await()
            val byteArray = fileResponse.body()?.bytes()
            if (byteArray == null) {
                fileResponse.errorBody()?.string() ?: "file $fileUrl is empty"
            } else {
                fileStorage.storeAsync(byteArray, fileUrl)?.message
            }
        }

        try {
            database.saveAsync(newsArray)
        } catch (e:Throwable){
            return@launch listener.onError(e)
        }

        if(fileErrorStrings.isEmpty()){
            listener.onSuccess(null)
        } else{
            listener.onSuccess(fileErrorStrings.joinToString (","))
        }
    }
}
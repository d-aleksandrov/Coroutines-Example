package guitariz.coroutinesexample

import guitariz.coroutinesexample.modules.FileStorageService
import guitariz.coroutinesexample.modules.NewsDao
import guitariz.coroutinesexample.modules.NewsNetwork
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FileCacheWorker(
        private val network: NewsNetwork,
        private val database: NewsDao,
        private val fileStorage: FileStorageService,
        private val listener: NewsCacheListener) {
    init {
        doAsync {
            val lastID: String = database.lastElement().firstOrNull()?.id
                    ?: return@doAsync listener.onError(Throwable("do not have last news id"))

            loadNews(lastID)
        }
    }

    private fun loadNews(lastID: String) {
        network.loadNews(lastID).enqueue(object : Callback<List<NewsModel>> {
            override fun onFailure(call: Call<List<NewsModel>>, t: Throwable) = listener.onError(t)

            override fun onResponse(call: Call<List<NewsModel>>, response: Response<List<NewsModel>>) {
                val loadedNews = response.body()
                        ?: return listener.onError(Throwable("cannot parse news load results"))

                if (loadedNews.isEmpty())
                    return listener.onSuccess()

                processLoadedNews(loadedNews)
            }
        })
    }

    private fun processLoadedNews(loadedNews: List<NewsModel>) {
        val files = loadedNews.mapNotNull(NewsModel::attachments).flatten()
        saveFiles(files) { error ->
            error?.let(listener::onError) ?: saveModels(loadedNews)
        }
    }

    private fun saveFiles(remainingFiles: List<String>, callback: (Throwable?) -> Unit) {
        val file = remainingFiles.firstOrNull() ?: return callback(null)

        network.downloadFile(file).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) = callback(t)

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                doAsync {
                    val loadedBytes = response.body()?.bytes()
                            ?: return@doAsync callback(Throwable("File $file is empty"))
                    fileStorage.store(loadedBytes, file) { error ->
                        error?.let(callback) ?: saveFiles(remainingFiles.minus(file), callback)
                    }
                }
            }
        })
    }

    private fun saveModels(models: List<NewsModel>) {
        doAsync {
            val error: Throwable? = try {
                database.save(models)
                null
            } catch (t: Throwable) {
                t
            }

            error?.let(listener::onError)
                    ?: listener.onSuccess()
        }
    }
}
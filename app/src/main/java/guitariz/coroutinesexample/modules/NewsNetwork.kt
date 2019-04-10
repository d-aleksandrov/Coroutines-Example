package guitariz.coroutinesexample.modules

import guitariz.coroutinesexample.NewsModel
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url
import okhttp3.ResponseBody
import kotlin.coroutines.suspendCoroutine

interface NewsNetwork{
    @GET("news/lastId=:lastId")
    fun loadNews(lastId:String): Call<List<NewsModel>>

    @GET("news/lastId=:lastId")
    fun loadNewsDeferred(lastId:String): Deferred<List<NewsModel>>

    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>
}
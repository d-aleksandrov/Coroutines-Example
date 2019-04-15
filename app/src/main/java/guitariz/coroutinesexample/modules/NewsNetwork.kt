package guitariz.coroutinesexample.modules

import guitariz.coroutinesexample.NewsModel
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface NewsNetwork{
    @GET("news/lastId=:lastId")
    fun loadNews(lastId:String): Call<List<NewsModel>>

    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>


    @GET("news/lastId=:lastId")
    fun loadNewsAsync(lastId:String): Deferred<Response<List<NewsModel>>>

    @GET
    fun downloadFileAsync(@Url fileUrl: String): Deferred<Response<ResponseBody>>
}
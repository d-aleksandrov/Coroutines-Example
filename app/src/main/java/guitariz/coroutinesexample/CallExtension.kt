package guitariz.coroutinesexample

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T>Call<T>.enqueue(callback:(T?, Throwable?)->Unit) = enqueue(object :Callback<T>{
    override fun onFailure(call: Call<T>, t: Throwable) = callback(null, t)
    override fun onResponse(call: Call<T>, response: Response<T>) = callback(response.body(), null)
})
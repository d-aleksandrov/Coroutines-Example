package guitariz.coroutinesexample.modules

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import guitariz.coroutinesexample.NewsModel
import kotlin.coroutines.suspendCoroutine

@Dao
interface NewsDao {
    @Query("SELECT * from NewsModel ORDER BY id DESC LIMIT 1")
    fun lastElement(): List<NewsModel>

    @Insert
    fun save(models: List<NewsModel>)
}
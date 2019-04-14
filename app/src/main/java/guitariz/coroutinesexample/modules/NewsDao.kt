package guitariz.coroutinesexample.modules

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import guitariz.coroutinesexample.NewsModel

@Dao
interface NewsDao {
    @Query("SELECT * from NewsModel ORDER BY id DESC LIMIT 1")
    fun lastElement(): List<NewsModel>

    @Insert
    fun save(models: List<NewsModel>)

    @Query("SELECT * from NewsModel ORDER BY id DESC LIMIT 1")
    suspend fun lastElementAsync(): List<NewsModel>

    @Insert
    suspend fun saveAsync(models: List<NewsModel>)
}
package guitariz.coroutinesexample

import androidx.room.Entity

@Entity
class NewsModel{
    var id:String = ""
    var attachments: List<String>? = null
}
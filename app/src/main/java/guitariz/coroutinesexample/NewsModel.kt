package guitariz.coroutinesexample

import android.arch.persistence.room.Entity

@Entity
class NewsModel{
    var id:String = ""
    var attachments: List<String>? = null
}
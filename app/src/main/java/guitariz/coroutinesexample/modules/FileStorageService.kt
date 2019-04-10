package guitariz.coroutinesexample.modules

interface FileStorageService {
    fun store(bytes: ByteArray, name: String, callback: (Throwable?) -> Unit)
}
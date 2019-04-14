package guitariz.coroutinesexample.modules

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface FileStorageService {
    fun store(bytes: ByteArray, name: String, callback: (Throwable?) -> Unit)
}

suspend fun FileStorageService.storeAsync(bytes: ByteArray, name: String)
        : Throwable? = suspendCoroutine { continuation -> store(bytes, name, continuation::resume) }
package guitariz.coroutinesexample

fun <T, V> Collection<T>.firstOfOrNull(block: (T) -> V?): V? {
    forEach { t ->
        block(t)?.let { return it }
    }
    return null
}
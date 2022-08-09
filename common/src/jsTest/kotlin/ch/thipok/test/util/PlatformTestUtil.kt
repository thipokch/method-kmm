package ch.thipok.test.util

actual object PlatformTestUtil {
    actual val platformName: String
        get() = "JS"

    actual fun createTempDir(prefix: String): String {
        TODO("Not yet implemented")
    }

    actual fun deleteTempDir(path: String) {
        TODO("Not yet implemented")
    }
}
package ch.thipok.test.util

import android.annotation.SuppressLint
import java.io.File
import java.nio.file.Files
import kotlin.io.path.absolutePathString

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual object PlatformTestUtil {
    actual val platformName: String
        get() = android.os.Build.MODEL ?: "AndroidTest JVM"

    @SuppressLint("NewApi")
    actual fun createTempDir(prefix: String): String {
        return Files.createTempDirectory("$prefix-android_tests").absolutePathString()
    }

    actual fun deleteTempDir(path: String) {
        File(path).deleteRecursively()
    }
}
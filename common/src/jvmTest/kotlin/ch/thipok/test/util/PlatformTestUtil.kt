package ch.thipok.test.util

import java.io.File
import java.nio.file.Files
import kotlin.io.path.absolutePathString

actual object PlatformTestUtil {
    actual val platformName: String
        get() = System.getProperty("java.vm.name")

    actual fun createTempDir(prefix: String): String {
        return Files.createTempDirectory("$prefix-jvm_tests").absolutePathString()
    }

    actual fun deleteTempDir(path: String) {
        File(path).deleteRecursively()
    }
}
package ch.thipok.test.util

import kotlinx.cinterop.cstr
import platform.UIKit.UIDevice

actual object PlatformTestUtil {
    actual val platformName: String
        get() = UIDevice().systemName

    actual fun createTempDir(prefix: String): String {
        // X is a special char which will be replace by mkdtemp template
        val mask = prefix.replace('X', 'Z', ignoreCase = true)
        val path = "${platform.Foundation.NSTemporaryDirectory()}$mask"
        platform.posix.mkdtemp(path.cstr)
        return path
    }

    actual fun deleteTempDir(path: String) {
        platform.Foundation.NSFileManager.defaultManager.removeItemAtURL(
            platform.Foundation.NSURL(
                fileURLWithPath = path
            ), null
        )
    }
}
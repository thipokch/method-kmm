package ch.thipok.test.util

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object PlatformTestUtil {
    val platformName: String

    fun createTempDir(prefix: String = CommonTestUtil.createRandomString(16)): String
    fun deleteTempDir(path: String)
}
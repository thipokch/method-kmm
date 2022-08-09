package ch.thipok.method.utils.di

import ch.thipok.test.ignore.IgnoreAndroidJsTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.koin.test.check.checkModules
import kotlin.test.Test

@Suppress("TestFunctionName")
@IgnoreAndroidJsTest
@ExperimentalCoroutinesApi
class KoinTest {

    @Test
    fun WHEN_initKoin_THEN_initModules() = runTest {
        initKoin {
            checkModules {
                withInstance(platformModules())
                withInstance(commonModules())
            }
        }
    }

}

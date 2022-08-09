package ch.thipok.method.utils.log

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import co.touchlab.kermit.crashlytics.setupCrashlyticsExceptionHook

@OptIn(ExperimentalKermitApi::class)
@Suppress("unused")
actual fun initKermit() {
    Logger.addLogWriter(
        CrashlyticsLogWriter(
            minSeverity = Severity.Error,
            minCrashSeverity = Severity.Warn,
            printTag = true,
        )
    )
    setupCrashlyticsExceptionHook(Logger)
}

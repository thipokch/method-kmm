package ch.thipok.method.utils.log

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter

@OptIn(ExperimentalKermitApi::class)
actual fun initKermit() {
    Logger.addLogWriter(CrashlyticsLogWriter())
}

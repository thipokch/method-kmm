package ch.thipok.method.utils.di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            platformModules(),
            commonModules()
        )
        logger(
            KermitKoinLogger(Logger.withTag("🪙 - koin"))
        )
    }

// called by iOS
fun initKoin() = initKoin {}

fun commonModules(): Module = module {
//    single { Repository() }
}

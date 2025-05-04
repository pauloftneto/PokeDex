package br.ftdev.core.analytics.di

import br.ftdev.core.analytics.AnalyticsTracker
import br.ftdev.core.analytics.EngineeringTracker
import br.ftdev.core.analytics.fake.FakeAnalytics
import br.ftdev.core.analytics.impl.AnalyticsTrackerImpl
import br.ftdev.core.analytics.impl.EngineeringTrackerImpl
import org.koin.dsl.module

val analyticsModule = module {
    single<AnalyticsTracker> { AnalyticsTrackerImpl(FakeAnalytics()) }
    single<EngineeringTracker> { EngineeringTrackerImpl() }
}
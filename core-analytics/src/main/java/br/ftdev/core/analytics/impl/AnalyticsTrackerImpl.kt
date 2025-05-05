package br.ftdev.core.analytics.impl

import android.os.Bundle
import br.ftdev.core.analytics.AnalyticsTracker
import br.ftdev.core.analytics.fake.FakeAnalytics
import br.ftdev.core.analytics.fake.FakeAnalytics.Companion.Event.SCREEN_VIEW
import br.ftdev.core.analytics.fake.FakeAnalytics.Companion.Param.SCREEN_NAME

class AnalyticsTrackerImpl(
  private val analytics: FakeAnalytics
) : AnalyticsTracker {
  override fun trackScreen(screenName: String, params: Map<String, String>) {
    val bundle = Bundle().apply {
      putString(SCREEN_NAME, screenName)
      params.forEach { (k, v) -> putString(k, v) }
    }
    analytics.logEvent(SCREEN_VIEW, bundle)
  }

  override fun trackEvent(eventName: String, params: Map<String, String>) {
    val bundle = Bundle().apply {
      params.forEach { (k, v) -> putString(k, v) }
    }
    analytics.logEvent(eventName, bundle)
  }
}
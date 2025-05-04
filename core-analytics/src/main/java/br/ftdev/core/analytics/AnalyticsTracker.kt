package br.ftdev.core.analytics

interface AnalyticsTracker {
  fun trackScreen(screenName: String, params: Map<String, String> = emptyMap())
  fun trackEvent(eventName: String, params: Map<String, String> = emptyMap())
}
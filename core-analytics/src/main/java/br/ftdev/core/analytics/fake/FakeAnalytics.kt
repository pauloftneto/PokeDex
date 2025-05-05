package br.ftdev.core.analytics.fake

import android.os.Bundle

class FakeAnalytics {

  private val _loggedEvents = mutableListOf<Pair<String, Bundle>>()

  fun logEvent(eventName: String, params: Bundle) {
    _loggedEvents += eventName to Bundle(params)
  }

  companion object {
    object Event {
      const val SCREEN_VIEW = "screen_view"
    }
    object Param {
      const val SCREEN_NAME = "screen_name"
    }
  }
}
package br.ftdev.core.analytics.impl

import br.ftdev.core.analytics.EngineeringTracker
import timber.log.Timber

class EngineeringTrackerImpl : EngineeringTracker {
  override fun trackInfo(event: String, params: Map<String, String>) {
    Timber.tag("ENG_INFO").i("$event — $params")
  }

  override fun trackError(event: String, error: Throwable?, params: Map<String, String>) {
    Timber.tag("ENG_ERROR").e(error, "$event — $params")
  }
}
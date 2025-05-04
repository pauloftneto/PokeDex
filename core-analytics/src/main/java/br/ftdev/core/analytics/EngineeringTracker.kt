package br.ftdev.core.analytics

interface EngineeringTracker {
  fun trackInfo(event: String, params: Map<String, String> = emptyMap())
  fun trackError(event: String, error: Throwable? = null, params: Map<String, String> = emptyMap())
}
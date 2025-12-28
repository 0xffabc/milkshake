package com.client.github.feature

object FeatureConfig {
  val config: HashMap<String, Boolean> = HashMap()
  val tabsData: HashMap<String, MutableList<String>> = HashMap()
}

open class Module(
  val featureGroup: String,
  val featureName: String,
  val default: Boolean = false
) {
  init {
    FeatureConfig.config.put(featureName, default)

    if (!FeatureConfig.tabsData.containsKey(featureGroup)) {
      FeatureConfig.tabsData.put(featureGroup, mutableListOf("Back", featureName))
    } else if ((FeatureConfig.tabsData.get(featureGroup)?.contains(featureName)?.not()) ?: true) {
      FeatureConfig.tabsData.get(featureGroup)!!.add(featureName)
    }
  }

  fun enabled(): Boolean = FeatureConfig.config.getOrDefault(featureName, false)
  fun disabled(): Boolean = enabled().not()

  fun enable() = FeatureConfig.config.put(featureName, true)
  fun disable() = FeatureConfig.config.put(featureName, false)
  fun invertState() = FeatureConfig.config.put(featureName, disabled())
}

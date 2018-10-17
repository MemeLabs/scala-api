package com.app.core.model.api

import akka.http.caching.LfuCache
import akka.http.caching.scaladsl.{ Cache, CachingSettings, LfuCacheSettings }
import com.app.core.model.api.polls.Poll
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object Caches {
  // Cache to rate limit a currency update
  val defaultCachingSettings: CachingSettings = CachingSettings(ConfigFactory.load())

  val activePollsCacheSettings: LfuCacheSettings =
    defaultCachingSettings.lfuCacheSettings
      .withInitialCapacity(25)
      .withMaxCapacity(500)
      .withTimeToLive(20.minutes)
      .withTimeToIdle(15.minutes)

  val pollsCache: Cache[String, Poll] = LfuCache(defaultCachingSettings.withLfuCacheSettings(activePollsCacheSettings))
  val usersPollsCache: Cache[String, String] = LfuCache(defaultCachingSettings.withLfuCacheSettings(activePollsCacheSettings))

}

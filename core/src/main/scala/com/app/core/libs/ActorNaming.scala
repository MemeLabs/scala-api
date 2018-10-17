package com.app.core.libs

import java.util.concurrent.atomic.AtomicInteger

/**
 *
 */
trait ActorNaming {
  private val num = new AtomicInteger(0)
  def name: String
  def actorName: String = s"$name-${num.getAndIncrement()}"
}

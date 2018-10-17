package com.app.jobs

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.app.core.model.ServiceJsonFormatters
import org.slf4j.LoggerFactory

/**
 * App
 */
object Bootstrap extends App
  with ServiceJsonFormatters {

  implicit val system = ActorSystem("jobs-application")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val ec = system.dispatcher

  val LOG = LoggerFactory.getLogger(this.getClass)

  LOG.info(s"Jobs server online")
}

package com.app.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.app.api.routes._
import com.app.api.routes.exception_handlers.{GenericExceptionHandler, ServiceExceptionHandler}
import com.app.core.model.ServiceJsonFormatters
import org.slf4j.LoggerFactory

/**
 * App
 */
object Bootstrap extends App
  with Dependencies
  with Routes
  with ServiceRoute
  with PollRoutes
  with ServiceExceptionHandler
  with GenericExceptionHandler
  with ServiceJsonFormatters {

  implicit val system = ActorSystem("application")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val ec = system.dispatcher

  val LOG = LoggerFactory.getLogger(this.getClass)

  Http().bindAndHandle(route, "0.0.0.0", 3000)

  LOG.info(s"API Server online at http://0.0.0.0:3000")
}

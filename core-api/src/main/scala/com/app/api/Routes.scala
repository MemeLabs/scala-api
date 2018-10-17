package com.app.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import akka.util.Timeout
import com.app.api.routes.ServiceRoute
import com.app.api.routes.exception_handlers.ServiceExceptionHandler
import com.app.core.model.ServiceJsonFormatters
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
 * Route definition of the services api
 */
trait Routes extends Directives
  with SprayJsonSupport
  with ServiceJsonFormatters {
  self: Dependencies with ServiceRoute with ServiceExceptionHandler ⇒

  implicit lazy val t: Timeout = 5.seconds

  implicit def myExceptionHandler: ExceptionHandler =
    genericHandler

  val opsRoute: Route =
    (pathEndOrSingleSlash | pathPrefix("ops" / "status")) {
      get {
        complete(StatusCodes.OK)
      }
    }

  //TODO: Wrap all routes in validation of sig hash
  val route: Route =
    opsRoute ~
      logRequest("V1 API") {
        pathPrefix("v1") { // V1 of the api
            pollRouteV1
        }
      }

}


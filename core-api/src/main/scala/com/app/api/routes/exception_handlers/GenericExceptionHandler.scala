package com.app.api.routes.exception_handlers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import com.app.core.model.ServiceJsonFormatters
import com.app.core.model.api.response.ApiFailure

/**
  *
  */
trait GenericExceptionHandler {
  self: Directives with SprayJsonSupport with ServiceJsonFormatters =>

  def genericHandler: ExceptionHandler = ExceptionHandler {
      case e: IllegalArgumentException ⇒
        extractUri { uri ⇒
          println(s"Request to $uri could not be handled normally, $e")
          complete(StatusCodes.BadRequest, ApiFailure(e.getMessage))
        }
      case e: NoSuchElementException ⇒
        extractUri { uri ⇒
          println(s"Request to $uri could not be handled normally, $e")
          complete(StatusCodes.NotFound, ApiFailure(e.getMessage))
        }
      case e: Exception ⇒
        extractUri { uri ⇒
          println(s"Request to $uri could not be handled normally, $e")
          complete(StatusCodes.InternalServerError, ApiFailure(e.getMessage))
        }
    }
}

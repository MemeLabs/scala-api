package com.app.api.routes.exception_handlers

import akka.http.scaladsl.server.ExceptionHandler

/**
  *
  */
trait ServiceExceptionHandler {
  def genericHandler: ExceptionHandler
}

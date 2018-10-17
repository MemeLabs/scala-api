package com.app.api.routes

import akka.http.scaladsl.server.Route

/**
 *
 */
trait ServiceRoute {
  def pollRouteV1: Route
}

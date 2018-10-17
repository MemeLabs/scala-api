package com.app.api

import akka.actor.{ActorSystem}
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext

/**
 * Service Dependencies such as actors, clients, and interface implementations
 */
trait Dependencies {

  implicit val system: ActorSystem
  implicit val materializer: Materializer
  implicit val ec: ExecutionContext

  lazy val config = ConfigFactory.load()
  lazy val log = LoggerFactory.getLogger("main")

}

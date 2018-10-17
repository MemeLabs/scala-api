package com.app.api.polls

import akka.actor.{Actor, ActorLogging, ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.app.core.libs.ActorNaming
import com.app.core.model.ServiceJsonFormatters
import scala.concurrent.{ExecutionContext}

object RandomActor extends ActorNaming {

  def name: String = "random-actor"

  def props() =
    Props(classOf[RandomActor])
  def actorOf()(implicit ref: ActorRefFactory): ActorRef =
    ref.actorOf(props(), actorName)

}

/**
  *
  */
class RandomActor()
  extends Actor with ActorLogging with ServiceJsonFormatters {
  implicit val system: ActorSystem = context.system
  implicit val ec: ExecutionContext = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def receive: Receive = {
    case m =>
      log.debug("Request received.")
      context.stop(self)
  }
}

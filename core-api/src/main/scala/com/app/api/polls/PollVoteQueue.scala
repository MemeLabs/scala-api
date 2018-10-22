package com.app.api.polls

import akka.actor.{Actor, ActorLogging, ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.app.core.libs.ActorNaming
import com.app.core.model.ServiceJsonFormatters
import com.app.core.model.api.Caches.pollsCache
import com.app.core.model.api.polls.Poll

import scala.concurrent.{ExecutionContext, Future}

object PollVoteQueue extends ActorNaming {

  def name: String = "random-actor"

  def props() =
    Props(classOf[PollVoteQueue])
  def actorOf()(implicit ref: ActorRefFactory): ActorRef =
    ref.actorOf(props(), actorName)

  case class VotePoll(userId: String, poll: Poll, options: List[String])
}

/**
  *
  */
class PollVoteQueue()
  extends Actor with ActorLogging with ServiceJsonFormatters {
  implicit val system: ActorSystem = context.system
  implicit val ec: ExecutionContext = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  import com.app.api.polls.PollVoteQueue.VotePoll

  def receive: Receive = {
    case vote: VotePoll =>
      log.debug("Request received.")
      val newResults = vote.poll.options ++ vote.options.map(o => o -> (vote.poll.options(o) + 1))
      val newPoll = vote.poll.copy(options = newResults, votes = vote.poll.votes ++ Map(vote.userId -> vote.options))
      pollsCache.remove(vote.poll.id)
      pollsCache.apply(vote.poll.id, () => Future(newPoll))
  }
}

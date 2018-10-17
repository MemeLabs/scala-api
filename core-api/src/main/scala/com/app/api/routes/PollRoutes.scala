package com.app.api.routes

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.app.api.Dependencies
import com.app.core.model.ServiceJsonFormatters
import com.app.core.model.api.Caches._
import com.app.core.model.api.polls.{CreatePoll, Poll, Vote}

import scala.concurrent.Future
import scala.util.{Failure, Success}
/**
 *
 */
trait PollRoutes {
  self: Dependencies with Directives with SprayJsonSupport with ServiceJsonFormatters ⇒

  def pollRouteV1: Route =
      path("poll") { // Create Polls
        post {
          entity(as[CreatePoll]) { cPoll =>
            // Check if an Active Poll already exists
            val userPoll = usersPollsCache
              .get("user_id")
              .getOrElse(Future.failed(new NoSuchElementException("Poll does not exist.")))

            onComplete(userPoll) {
              case Success(pollId) => // Poll already exists
                failWith(new IllegalArgumentException(s"Active Poll already exists for this user: $pollId."))
              case Failure(e) => // Create Poll
                val pollId = UUID.randomUUID().toString
                usersPollsCache.apply("user_id",() => Future.successful(pollId))
                val poll = Poll(pollId,
                  cPoll.subject,
                  cPoll.options.map(o => o -> 0).toMap,
                  cPoll.multi_vote,
                  Map.empty[String,List[String]])
                pollsCache.apply(pollId,() => Future.successful(poll))
                complete(StatusCodes.Created, poll)
            }
          }
        }
      } ~
      path("poll" / Segment) { pollId =>  // Get / Delete Polls
        get {
           // Get Poll from Cache
           val pollF = pollsCache
             .get(pollId)
             .getOrElse(Future.failed(new NoSuchElementException("Poll does not exist.")))

           onComplete(pollF) {
             case Success(poll) => // Respond with Poll
               complete(poll)
             case Failure(e) => // Respond with Not Found
               failWith(e)
           }
        } ~
          delete {
            // Check if an Active Poll already exists
            val userPoll = usersPollsCache
              .get("user_id")
              .getOrElse(Future.failed(new NoSuchElementException("Poll does not exist.")))

            onComplete(userPoll) {
              case Success(userPollId) if pollId != userPollId => // Users Poll Doesn't Equal Request
                failWith(new IllegalArgumentException(s"Poll Id is incorrect for this user: $pollId."))
              case Failure(e) => // Respond with Not Found
                failWith(e)
              case Success(userPollId) => // Users Poll Doesn't Equal Request
                usersPollsCache.remove("user_id")
                pollsCache.remove(pollId)
                complete(StatusCodes.NoContent)
            }
        }
      } ~
      path("poll" / Segment / "vote") { poll_id => // Vote on a Poll
        post {
          entity(as[Vote]) { vote =>
            // Get Poll from Cache
            val pollF = pollsCache
              .get(poll_id)
              .getOrElse(Future.failed(new NoSuchElementException("Poll does not exist.")))

            onComplete(pollF) {
              case Success(poll) =>
                // Check if user Voted
                poll.votes.get("user_id") match {
                  case Some(voted) => // Already Voted, Error
                    failWith(new IllegalArgumentException("User has already voted on this poll."))
                  case None if !(poll.options forall(vote.options contains)) => // Vote option(s) is/are not valid
                    failWith(new IllegalArgumentException("Vote option is not valid."))
                  case None => // Vote
                    val newResults = if(poll.multi_vote) { // Single or Multi-vote
                      poll.options ++ vote.options.map(o => o -> (poll.options(o) + 1))
                    } else {
                        poll.options ++ Map(vote.options.head -> (poll.options(vote.options.head)+1))
                    }
                    val newPoll = poll.copy(options = newResults,
                      votes = poll.votes ++ Map("user_id" -> vote.options))
                    //TODO: Remove Race Condition
                    pollsCache.remove(poll.id)
                    pollsCache.apply(poll.id, () => Future(newPoll))
                    complete(StatusCodes.NoContent, newPoll)
                }
              case Failure(e) => // Respond with Not Found
                failWith(e)
            }
          }
        }
      }
}


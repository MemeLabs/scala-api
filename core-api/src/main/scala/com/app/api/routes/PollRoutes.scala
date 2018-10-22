package com.app.api.routes

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Directive1, Directives, Route}
import com.app.api.Dependencies
import com.app.api.polls.PollVoteQueue.VotePoll
import com.app.core.model.ServiceJsonFormatters
import com.app.core.model.api.Caches._
import com.app.core.model.api.polls.{CreatePoll, Poll, Vote}
import com.auth0.jwt.JWT

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
          jwtAuthenticate { userId =>
            entity(as[CreatePoll]) { cPoll =>
              // Check if an Active Poll already exists
              val userPoll = usersPollsCache
                .get(userId)
                .getOrElse(Future.failed(new NoSuchElementException("Poll does not exist.")))

              onComplete(userPoll) {
                case Success(pollId) => // Poll already exists
                  failWith(new IllegalArgumentException(s"Active Poll already exists for this user: $pollId."))
                case Failure(e) => // Create Poll
                  val pollId = UUID.randomUUID().toString
                  usersPollsCache.apply(userId, () => Future.successful(pollId))
                  val poll = Poll(pollId,
                    cPoll.subject,
                    cPoll.options.map(o => o -> 0).toMap,
                    cPoll.multi_vote,
                    Map.empty[String, List[String]])
                  pollsCache.apply(pollId, () => Future.successful(poll))
                  complete(StatusCodes.Created, poll)
              }
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
            jwtAuthenticate { userId =>
              // Check if an Active Poll already exists
              val userPoll = usersPollsCache
                .get(userId)
                .getOrElse(Future.failed(new NoSuchElementException("Poll does not exist.")))

              onComplete(userPoll) {
                case Success(userPollId) if pollId != userPollId => // Users Poll Doesn't Equal Request
                  failWith(new IllegalArgumentException(s"Poll Id is incorrect for this user: $pollId."))
                case Failure(e) => // Respond with Not Found
                  failWith(e)
                case Success(userPollId) => // Users Poll Doesn't Equal Request
                  usersPollsCache.remove(userId)
                  pollsCache.remove(pollId)
                  complete(StatusCodes.NoContent)
              }
            }
        }
      } ~
      path("poll" / Segment / "vote") { poll_id => // Vote on a Poll
        post {
          jwtAuthenticate { userId =>
            entity(as[Vote]) { vote =>
              // Get Poll from Cache
              val pollF = pollsCache
                .get(poll_id)
                .getOrElse(Future.failed(new NoSuchElementException("Poll does not exist.")))

              onComplete(pollF) {
                case Success(poll) =>
                  // Check if user Voted
                  poll.votes.get(userId) match {
                    case Some(voted) => // Already Voted, Error
                      failWith(new IllegalArgumentException("User has already voted on this poll."))
                    case None if !(vote.options forall (poll.options contains)) => // Vote option(s) is/are not valid
                      failWith(new IllegalArgumentException("Vote option is not valid."))
                    case None if !poll.multi_vote && vote.options.length > 1 => // Vote
                      failWith(new IllegalArgumentException("Poll is not a multi-vote poll."))
                    case None =>
                      pollVoteQueue ! VotePoll(userId, poll, vote.options)
                      complete(StatusCodes.Accepted)
                  }
                case Failure(e) => // Respond with Not Found
                  failWith(e)
              }
            }
          }
        }
      }

  def jwtAuthenticate: Directive1[String] = {
    for {
      header <- optionalHeaderValueByName("jwt")
      token <- checkJWTAuthorization(header)
    } yield token
  }

  def checkJWTAuthorization(a: Option[String]): Directive1[String] = a match {
    case Some(jwt) => provide(JWT.decode(jwt).getClaim("id").asString())
    case None => reject(AuthenticationFailedRejection(AuthenticationFailedRejection.CredentialsMissing, HttpChallenge("Basic", "JWT")))
    case _ => reject(AuthenticationFailedRejection(AuthenticationFailedRejection.CredentialsRejected, HttpChallenge("Basic", "JWT")))
  }

}


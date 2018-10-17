package com.app.core.model

import com.app.core.model.api._
import com.app.core.model.api.polls.{ CreatePoll, Poll, Vote }
import com.app.core.model.api.response._
import org.joda.time.DateTime
import org.joda.time.format.{ DateTimeFormatter, ISODateTimeFormat }
import spray.json._

import scala.collection.JavaConverters._

/**
 * Json formatters for all service models and related things
 */
trait ServiceJsonFormatters extends DefaultJsonProtocol {

  // Joda - DateTime
  val formatter: DateTimeFormatter = ISODateTimeFormat.dateTime()
  implicit val jodaDateTimeJFConverter: RootJsonFormat[DateTime] = new RootJsonFormat[DateTime] {
    override def write(obj: DateTime): JsValue = {
      formatter.print(obj).toJson
    }
    override def read(js: JsValue): DateTime = {
      formatter.parseDateTime(js.convertTo[String])
    }
  }

  // Api
  implicit val apiMetaJF: RootJsonFormat[ApiMeta] = jsonFormat2(ApiMeta)
  implicit val voteJF: RootJsonFormat[Vote] = jsonFormat1(Vote)
  implicit val createPollJF: RootJsonFormat[CreatePoll] = jsonFormat3(CreatePoll)

  implicit val pollJF: RootJsonFormat[Poll] = new RootJsonFormat[Poll] {
    override def write(obj: Poll): JsValue = {
      JsObject(
        "id" -> obj.id.toJson,
        "subject" -> obj.subject.toJson,
        "options" -> obj.options.toJson,
        "multi_vote" -> obj.multi_vote.toJson
      )
    }
    override def read(js: JsValue): Poll = ???
  }

  // Api Responses
  implicit def apiResponseJF[T: JsonFormat, K: JsonFormat] = new RootJsonFormat[ApiResponse[T, K]] {
    def write(t: ApiResponse[T, K]): JsValue = {
      val fields: scala.collection.mutable.Map[String, JsValue] = scala.collection.mutable.Map.empty[String, JsValue]
      fields.put("response", t.response.toJson)
      t.meta.map(m ⇒ fields.put("meta", m.toJson))
      t.request.map(m ⇒ fields.put("request", m.toJson))
      JsObject(fields.toMap)
    }
    def read(json: JsValue): ApiResponse[T, K] = {
      json.asJsObject.fields.get("response") match {
        case Some(v) ⇒ ApiResponse[T, K](
          v.convertTo[T],
          json.asJsObject.fields.get("request").map(_.convertTo[K]),
          json.asJsObject.fields.get("meta").map(_.convertTo[ApiMeta]))
        case None ⇒ throw DeserializationException(s"Improperly formatted ApiResponse")
      }
    }
  }
  implicit val apiErrorJF: RootJsonFormat[ApiFailure] = jsonFormat1(ApiFailure)

}

package models


import play.api.libs.json._
import play.api.libs.functional.syntax._
/**
 * Created by salho on 30.07.15.
 */
case class Message(msg: String)

object Message {
  implicit val messageFormat = Json.format[Message]
}


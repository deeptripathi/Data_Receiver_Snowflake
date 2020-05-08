package helper

import play.api.libs.functional.syntax._
import play.api.libs.json._

import models.AndroidRequest

object JsonParser {

  case class InvalidJsonException(message: String) extends Exception(message)

  def read[A](reads: Reads[A], json: JsValue): A = {
    json.validate[A](reads) match {

      case s: JsSuccess[A] => s.get
      case e: JsError =>
        throw InvalidJsonException("Cannot parse json " + e.toString)
    }
  }

  final val androidRequestReads: Reads[AndroidRequest] = (
    (JsPath \ "person_id").read[Long] and
      (JsPath \ "device_id").read[String] and
      (JsPath \ "android_id").read[String] and
      (JsPath \ "type").read[Int] and
      (JsPath \ "version").read[String] and
      (JsPath \ "is_pre_install").read[Boolean] and
      (JsPath \ "data").readNullable[List[JsObject]]
    ) (AndroidRequest.apply _)


}

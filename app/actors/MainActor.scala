package actors

import Handler.HandlerFactory
import actors.MainActor.ProcessRequest
import akka.actor.{Actor, ActorSystem}
import javax.inject.Inject
import play.api.libs.json._
import play.api.mvc.Result
import play.api.mvc.Results._
import helper.JsonParser
import models.AndroidRequest
import Handler._
import play.api.libs.json.Json.toJson

import scala.concurrent.ExecutionContext
import scala.util.control.Breaks.breakable

object MainActor {

  case class ProcessRequest(request: JsValue)

}

class MainActor @Inject() (implicit ec : ExecutionContext) extends  Actor {

  implicit val system: ActorSystem = ActorSystem()

  override def receive: Receive = {

    case ProcessRequest(request: JsValue) => Result

      val androidRequest = JsonParser.read[AndroidRequest](JsonParser.androidRequestReads, request)

      val handler = new HandlerFactory().initializeHandler(androidRequest)

      androidRequest.data.getOrElse(Seq()).filter(isNonEmptyJsObject).foreach(data => sendDataToSnowFlake(handler,data))

      //! means “fire-and-forget”, e.g. send a message asynchronously and return immediately. Also known as tell.
      // ? sends a message asynchronously and returns a Future representing a possible reply. Also known as ask.
      sender ! ResponseSuccess(request)
  }

  def ResponseSuccess(request: JsValue): Result = Ok(request)

  private def sendDataToSnowFlake(handler: Handler, data: JsValue): Unit = breakable {

    if ( handler.dataRepositoryConfig.transferToSnowFlake) {
      handler.avero(data).foreach(sqlModel => {
        sender ! ResponseSuccess(sqlModel)
      })
    }else {
      sender ! ResponseSuccess(createJson("404",Some("Cannot transfer to snowflake")))
    }


  }
  private def isNonEmptyJsObject(jsValue: JsValue): Boolean =
    !jsValue.asOpt[JsObject].exists(_.keys.isEmpty)


  private def createJson(status: String, description: Option[String] = None): JsObject = {
    Json.obj(
      "status" -> toJson(status),
      "description" -> toJson(description)
    )
  }
}


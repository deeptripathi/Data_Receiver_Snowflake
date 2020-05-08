package controllers

import actors.MainActor.ProcessRequest
import javax.inject._
import play.api.mvc._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.json.Json.toJson
import models.HelloWorld
import javax.ws.rs.core.MediaType.{APPLICATION_JSON, APPLICATION_OCTET_STREAM}
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(@Named("main-actor") mainActor :ActorRef,cc: ControllerComponents)
                              ( implicit ec: ExecutionContext) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index: Action[AnyContent] = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def hello(module: String): Action[AnyContent] = Action {

    Ok(Json.toJson(HelloWorld(module)))

  }

  def receiveData(): Action[AnyContent] = Action.async {
    request =>
      request.contentType.map(_.toLowerCase) match {
        case Some(APPLICATION_JSON) => processJsonReqeust(request)
      }
  }

  def processJsonReqeust(request: Request[AnyContent]): Future[Result] = {
    request.body.asJson
      .map(startMainActor)
      .getOrElse(Future(BadRequest(createJson("error", Some("JSON request cannot be decoded")))))
  }

  private def startMainActor(request: JsValue): Future[Result] = {
    implicit val duration: Timeout = 20 seconds

    //! means “fire-and-forget”, e.g. send a message asynchronously and return immediately. Also known as tell.
    // ? sends a message asynchronously and returns a Future representing a possible reply. Also known as ask.

    (mainActor ? ProcessRequest(request)).mapTo[Result]

  }

  private def createJson(status: String, description: Option[String] = None): JsObject = {
    Json.obj(
      "status" -> toJson(status),
      "description" -> toJson(description)
    )
  }
}

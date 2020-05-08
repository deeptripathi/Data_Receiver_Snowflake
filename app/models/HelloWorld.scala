package models

import play.api.libs.json.Json

case class HelloWorld(module : String)

object HelloWorld{

  implicit  val writes = Json.writes[HelloWorld]
}

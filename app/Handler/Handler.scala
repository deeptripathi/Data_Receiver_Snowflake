package Handler

//import java.nio.ByteBuffer

import models.AndroidRequest
import play.api.libs.json.{JsObject, JsValue}

trait Handler {



  protected val androidRequest: AndroidRequest

  protected def getDataRepositoryItem(data: JsValue): Option[JsObject] =
    convertToJson(data)


  def convertToJson(data: JsValue): Option[JsObject] = None

  //def avero(data: JsValue): Option[JsObject] = None
  def avero(data: JsValue): Option[JsObject] = None

  def convertToJsonForKafkaProxy(data: JsValue): JsValue

  val dataRepositoryConfig: DataRepositoryConfig = DataRepositoryConfig(
    transferToSnowFlake = false
  )

}

case class DataRepositoryConfig(

                               transferToSnowFlake : Boolean

                               )

package Handler

import java.nio.ByteBuffer

import models.AndroidRequest
import play.api.libs.json.{JsObject, JsValue}

trait Handler {

  val TAG_ITEM_HASH = "hash"
  val TAG_ITEM_RANGE = "range"
  val TAG_ITEM_GSI_PERSON_HASH = "gsi_person_hash"
  val TAG_ITEM_DATA = "data"
  val TAG_ITEM_PERSON_ID = "person_id"
  val TAG_ITEM_FORMATTED_PHONE = "formatted_phone"

  def dataRepositoryRangePrefix(): String = if (!androidRequest.isPreInstall) "post-" else ""

  protected val androidRequest: AndroidRequest

  protected def getDataRepositoryItem(data: JsValue): Option[JsObject] =
    convertToJson(data)


  def convertToJson(data: JsValue): Option[JsObject] = None

  val dataRepositoryConfig: DataRepositoryConfig = DataRepositoryConfig(
    transferToSnowFlake = false
  )

}

case class DataRepositoryConfig(

                               transferToSnowFlake : Boolean

                               )

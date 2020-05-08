package Handler

import models.AndroidRequest
import play.api.libs.json.{JsObject, JsValue, Json}

class DrmInfoHandler (request: AndroidRequest) extends  Handler {
  override protected val androidRequest: AndroidRequest = request

  override val dataRepositoryConfig: DataRepositoryConfig = DataRepositoryConfig(
    transferToSnowFlake = true
  )

  override def convertToJson(data: JsValue): Option[JsObject] = {

    Some(JsObject(Seq(
      "person_id" -> Json.toJson(androidRequest.personId),
      "device_id" -> Json.toJson(androidRequest.deviceId),
      "common_pssh_device_id" -> Json.toJson((data \ "common_pssh_device_id").asOpt[String]),
      "clearkey_device_id" -> Json.toJson((data \ "clearkey_device_id").asOpt[String]),
      "playready_device_id" -> Json.toJson((data \ "playready_device_id").asOpt[String]),
      "widevine_device_id" -> Json.toJson((data \ "widevine_device_id").asOpt[String]),
      "server_time" -> Json.toJson(System.currentTimeMillis() / 1000), // unix timestamp
    )))

  }

}

package models

import play.api.libs.json.JsValue

case class AndroidRequest(
                     personId : Long,
                     deviceId : String ,
                     androidId : String ,
                     dataType : Int ,
                     version : String ,
                     isPreInstall : Boolean,
                     data : Option[Seq[JsValue]])

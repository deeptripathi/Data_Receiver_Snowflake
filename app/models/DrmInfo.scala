package models

import java.sql.Timestamp

case class DrmInfo(
                    id: Option[Long],
                    personId: Long,
                    deviceId: String,
                    commonPsshDeviceId: Option[String],
                    clearkeyDeviceId: Option[String],
                    playreadyDeviceId: Option[String],
                    widevineDeviceId: Option[String],
                    serverTime: Timestamp
                  )

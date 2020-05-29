package helper

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.{LocalDateTime, ZoneId}



object TimeHelper {
//  val EPOCH_TIMESTAMP = Timestamp.valueOf("1970-01-01 00:00:01")
//  val SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//
//  def getUnixTimestamp(): Long = System.currentTimeMillis() / 1000
//
//  def formatDate(unixTime: Long): Timestamp =
//    toMySqlTime(Timestamp.valueOf(SQL_DATE_FORMAT.format(unixTime * 1000)))
//
//  def formatDateOpt(unixTime: Option[Long]): Option[Timestamp] =
//    if (unixTime.isDefined) Some(formatDate(unixTime.get)) else None
//
//  def formatDateEpoch(unixTime: Option[Long]): Timestamp =
//    unixTime.filter(_ > 0)
//      .map(formatDate)
//      .getOrElse(EPOCH_TIMESTAMP)
//
//  def formatDate(unixTime: Long, convertToLocal: Boolean): Timestamp =
//    if (convertToLocal) toLocalTime(formatDate(unixTime)) else formatDate(unixTime)
//
//
//
//
//
//
//  def toLocalTime(timestamp: Option[Timestamp]): Option[Timestamp] =
//    if (timestamp.isDefined) Some(toLocalTime(timestamp.get)) else None
//
//  def convertTime(time: LocalDateTime, fromTimezone: String, toTimezone: String): LocalDateTime = {
//    val fromTime = time.atZone(ZoneId.of(fromTimezone))
//    fromTime.withZoneSameInstant(ZoneId.of(toTimezone)).toLocalDateTime
//  }
//
//  def toSecond(timestamp: Timestamp): Long = timestamp.getTime / 1000
}

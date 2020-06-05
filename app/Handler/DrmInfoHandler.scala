package Handler

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar

import helper.KafkaProducerWrapper
import models.{AndroidRequest, DrmInfo}
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import play.api.Logger
import play.api.libs.json.{JsNull, JsObject, JsValue, Json}

import scala.collection.JavaConverters._
import scala.concurrent.Promise
import scala.io.Source
import scala.util.{Failure, Success}
import com.sksamuel.avro4s.AvroSchema
import com.sksamuel.avro4s.AvroOutputStream
import scalaj.http.{Http, HttpOptions}




class DrmInfoHandler(request: AndroidRequest) extends Handler {


  val accessLogger: Logger = Logger(this.getClass)
  implicit val formats = net.liftweb.json.DefaultFormats

  override protected val androidRequest: AndroidRequest = request

  override val dataRepositoryConfig: DataRepositoryConfig = DataRepositoryConfig(
    transferToSnowFlake = true
  )

  def getCurrentdateTimeStamp: Timestamp = {
    val today: java.util.Date = Calendar.getInstance.getTime
    val timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val now: String = timeFormat.format(today)
    val re = java.sql.Timestamp.valueOf(now)
    re
  }

  override def convertToJson(data: JsValue): Option[JsObject] = {

    Some(JsObject(Seq(
      "person_id" -> Json.toJson(androidRequest.personId),
      "device_id" -> Json.toJson(androidRequest.deviceId),
      "common_pssh_device_id" -> Json.toJson((data \ "common_pssh_device_id").asOpt[String]),
      "clearkey_device_id" -> Json.toJson((data \ "clearkey_device_id").asOpt[String]),
      "playready_device_id" -> Json.toJson((data \ "playready_device_id").asOpt[String]),
      "widevine_device_id" -> Json.toJson((data \ "widevine_device_id").asOpt[String]),
      "server_time" -> Json.toJson(System.currentTimeMillis() / 1000) // unix timestamp

    )))

  }


  import play.api.libs.json.Json.toJson


  override def convertToJsonForKafkaProxy(data: JsValue): JsValue = {

    val jsonObject = Json.toJson(
      Map(
        "value_schema_id" -> toJson("84"),
        "records" ->
         Json.arr(
          toJson(
            Map("value" ->
              Map(

                "source" -> toJson("DataReceiver"),
                "messageId" -> toJson("1.d.id"),
                "timestamp" -> toJson(1591333550),
                "keyspaceName" -> toJson("snowflake.keyspace"),
                "tableName" -> toJson("DrmInfo"),
                "operation" -> toJson("INSERT"),

                 "record" -> toJson(Map("package_name" -> toJson("pkg_name"),"user_id" -> toJson(1) , "last_uploaded_at" ->toJson( Map("long" -> toJson(159133355)) )))
              )
            )
          )
        )
      )
    )

    jsonObject
  }

  override def avero(data: JsValue): Option[JsObject] = {

    val props: Map[String, AnyRef] = Map(
      "bootstrap.servers" -> "localhost:9092",
      "group.id" -> "CountryCounter",
      "key.serializer" -> "io.confluent.kafka.serializers.KafkaAvroSerializer",
      "value.serializer" -> "io.confluent.kafka.serializers.KafkaAvroSerializer",
      "schema.registry.url" -> "http://localhost:8081"

    )

    val drmInfoSchema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/DrmInfo.avsc")).mkString)

    val schemar = AvroSchema[DrmInfo]

    val drmInfoRecord: GenericRecord = new GenericData.Record(drmInfoSchema)
    drmInfoRecord.put("personId", androidRequest.personId)
    drmInfoRecord.put("deviceId", androidRequest.deviceId)
    drmInfoRecord.put("commonPsshDeviceId", (data \ "common_pssh_device_id").asOpt[String].orNull)
    drmInfoRecord.put("clearkeyDeviceId", (data \ "clearkey_device_id").asOpt[String].orNull)
    drmInfoRecord.put("playreadyDeviceId", (data \ "playready_device_id").asOpt[String].orNull)
    drmInfoRecord.put("widevineDeviceId", (data \ "widevine_device_id").asOpt[String].orNull)
    drmInfoRecord.put("serverTime", System.currentTimeMillis() / 1000)






    val drmInfoModel : DrmInfo = new DrmInfo(
      None,
      androidRequest.personId,
      androidRequest.deviceId,
      (data \ "common_pssh_device_id").asOpt[String],
      (data \ "common_pssh_device_id").asOpt[String],
      (data \ "common_pssh_device_id").asOpt[String],
      (data \ "common_pssh_device_id").asOpt[String],
      System.currentTimeMillis() / 1000)




//    val result = Http("http://localhost:8082/topics/payment").postData("""{"value_schema_id": "63","records": [{"value": {"username": "testUser"}},{"value": {"username": "testUser2"}}]}""")
//      .header("Content-Type", "application/vnd.kafka.avro.v1+json")
//      .header("Charset", "UTF-8")
//      .option(HttpOptions.readTimeout(10000)).asString
//    accessLogger.info(result.toString)


    val result = Http("http://localhost:8082/topics/payment").postData(
      convertToJsonForKafkaProxy(data).toString()

    )
      .header("Content-Type", "application/vnd.kafka.avro.v1+json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(10000)).asString

    accessLogger.info(result.toString)



 //   val producer = new KafkaProducer[Int, GenericRecord](props.asJava)
    //val producer = new KafkaProducer[Int, DrmInfo](props.asJava)

  //  val record = new ProducerRecord("DrmInfo", 1, GenericRecord)

  //  val promise = Promise[RecordMetadata]()

    // producer.send(record, producerCallback(promise))
   // promise.future

    val producer : KafkaProducerWrapper = new KafkaProducerWrapper("DrmInfo", 1, drmInfoRecord)
    producer.send()

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

  private def producerCallback(promise: Promise[RecordMetadata]): Callback = {

    new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {


        val result = if (exception == null) {
          accessLogger.info("offset - " + metadata.offset())
          accessLogger.info("topic - " + metadata.topic())
          accessLogger.info("partition - " + metadata.partition())
          Success(metadata)
        }
        else {
          accessLogger.error(exception.printStackTrace().toString)
          Failure(exception)
        }

        promise.complete(result)

      }
    }

  }

}

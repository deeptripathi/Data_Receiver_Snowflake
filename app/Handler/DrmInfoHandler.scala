package Handler

import java.sql.Timestamp

import models.{AndroidRequest, DrmInfo}
import play.api.libs.json.{JsObject, JsValue, Json}
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import play.api.Logger

import scala.concurrent.Promise
import scala.io.Source
import scala.collection.JavaConverters._
import net.liftweb.json._
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.sql.{PreparedStatement, Timestamp, Types}
import java.util.Calendar



class DrmInfoHandler(request: AndroidRequest) extends Handler {


  val accessLogger: Logger = Logger(this.getClass)
  implicit val formats = net.liftweb.json.DefaultFormats

  override protected val androidRequest: AndroidRequest = request

  override val dataRepositoryConfig: DataRepositoryConfig = DataRepositoryConfig(
    transferToSnowFlake = true
  )

  def getCurrentdateTimeStamp: Timestamp ={
    val today:java.util.Date = Calendar.getInstance.getTime
    val timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val now:String = timeFormat.format(today)
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
      "server_time" -> Json.toJson(System.currentTimeMillis() / 1000), // unix timestamp
    )))

  }

  override def avero(data: JsValue): Option[JsObject] = {


    val drmInfoModel = DrmInfo(
      None,
      androidRequest.personId,
      androidRequest.deviceId,
      (data \ "common_pssh_device_id").asOpt[String],
      (data \ "clearkey_device_id").asOpt[String],
      (data \ "playready_device_id").asOpt[String],
      (data \ "widevine_device_id").asOpt[String],
      getCurrentdateTimeStamp

    )

    val props: Map[String, AnyRef] = Map(
      "bootstrap.servers" -> "localhost:9092",
      "group.id" -> "CountryCounter",
      "key.serializer" -> "io.confluent.kafka.serializers.KafkaAvroSerializer",
      "value.serializer" -> "io.confluent.kafka.serializers.KafkaAvroSerializer",
      "schema.registry.url" -> "http://localhost:8081"

    )


    val drmInfoSchema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/DrmInfo.avsc")).mkString)



    val drmInfoRecord: GenericRecord = new GenericData.Record(drmInfoSchema)
    drmInfoRecord.put("personId" , drmInfoModel.personId)
    drmInfoRecord.put("deviceId" ,  drmInfoModel.deviceId)
    drmInfoRecord.put("commonPsshDeviceId" , drmInfoModel.commonPsshDeviceId.orNull)
    drmInfoRecord.put("clearkeyDeviceId" ,  drmInfoModel.clearkeyDeviceId.orNull)
    drmInfoRecord.put("playreadyDeviceId" , drmInfoModel.playreadyDeviceId.orNull)
    drmInfoRecord.put("widevineDeviceId" , drmInfoModel.widevineDeviceId.orNull)
    drmInfoRecord.put("serverTime" , getCurrentdateTimeStamp)





    //val schema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/test.avsc")).mkString)

    // Create avro generic record object
    //val genericUser: GenericRecord = new GenericData.Record(schema)

    //Put data in that generic record
//    genericUser.put("id", 1)
//    genericUser.put("name", "test_name")
//    genericUser.put("email", null)
//    genericUser.put("address", "address")

    val producer = new KafkaProducer[Int, GenericRecord](props.asJava)

    val record = new ProducerRecord("DrmInfo", 1, drmInfoRecord)
    val promise = Promise[RecordMetadata]()

    producer.send(record, producerCallback(promise))
    //val v = promise.future
    //accessLogger.info("test")


    //    // Serialize generic record into byte array
    //    val writer = new SpecificDatumWriter[GenericRecord](schema)
    //    val out = new ByteArrayOutputStream()
    //    val encoder: BinaryEncoder = EncoderFactory.get().binaryEncoder(out, null)
    //    writer.write(genericUser, encoder)
    //    encoder.flush()
    //    out.close()
    //
    //    val serializedBytes: Array[Byte] = out.toByteArray()
    //
    //
    //    //val queueMessage = new KeyedMessage[String, Array[Byte]](topic, serializedBytes)
    //
    //    val producerRecord = new ProducerRecord[String, GenericRecord]("test_topic", genericUser)
    //
    //    producer.send(producerRecord, new Callback() {
    //      override def onCompletion(recordMetadata: RecordMetadata, e: Exception): Unit = {
    //
    //      }
    //    })


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

  private def producerCallback(promise: Promise[RecordMetadata]) : Callback = {

    new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {

        if (exception == null) {
          accessLogger.info("offset - " + metadata.offset())
          accessLogger.info("topic - " + metadata.topic())
          accessLogger.info("partition - " + metadata.partition())
        }
        else {
          accessLogger.error(exception.printStackTrace().toString)
        }





        //promise.complete(result)
      }
    }

  }

}

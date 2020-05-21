package Handler

import java.io.ByteArrayOutputStream
import java.util.{Properties, UUID}

import models.AndroidRequest
import play.api.libs.json.{JsObject, JsValue, Json}
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.io.{BinaryEncoder, EncoderFactory}
import org.apache.avro.specific.SpecificDatumWriter
import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import org.apache.kafka.clients.producer.{Producer, ProducerConfig}

import scala.io.Source


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

  override def avero(data: JsValue): Option[JsObject] = {

//     val props = new Properties()
//
//    props.put("metadata.broker.list", "localhost:9092")
//    props.put("message.send.max.retries", "5")
//    props.put("request.required.acks", "-1")
//    props.put("serializer.class", "kafka.serializer.DefaultEncoder")
//    props.put("client.id", UUID.randomUUID().toString())
//
//
//
//     val producer = new Producer[String, Array[Byte]](new ProducerConfig(props))



    val schema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/test.avsc")).mkString)


    // Create avro generic record object
    val genericUser: GenericRecord = new GenericData.Record(schema)

    //Put data in that generic record
    genericUser.put("id", 1)
    genericUser.put("name", "test_name")
    genericUser.put("email", null)

    // Serialize generic record into byte array
    val writer = new SpecificDatumWriter[GenericRecord](schema)
    val out = new ByteArrayOutputStream()
    val encoder: BinaryEncoder = EncoderFactory.get().binaryEncoder(out, null)
    writer.write(genericUser, encoder)
    encoder.flush()
    out.close()

    val serializedBytes: Array[Byte] = out.toByteArray()



//
//
//
//    Some(JsObject(Seq(
//      "person_id" -> Json.toJson(androidRequest.personId),
//      "device_id" -> Json.toJson(androidRequest.deviceId),
//      "common_pssh_device_id" -> Json.toJson((data \ "common_pssh_device_id").asOpt[String]),
//      "clearkey_device_id" -> Json.toJson((data \ "clearkey_device_id").asOpt[String]),
//      "playready_device_id" -> Json.toJson((data \ "playready_device_id").asOpt[String]),
//      "widevine_device_id" -> Json.toJson((data \ "widevine_device_id").asOpt[String]),
//      "server_time" -> Json.toJson(System.currentTimeMillis() / 1000), // unix timestamp
//    )))

  }

}

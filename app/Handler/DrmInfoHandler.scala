package Handler


import models.AndroidRequest
import play.api.libs.json.{JsObject, JsValue, Json}
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}


import scala.io.Source
import scala.collection.JavaConverters._

class DrmInfoHandler(request: AndroidRequest) extends Handler {


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


    val props: Map[String, AnyRef] = Map(
      "bootstrap.servers" -> "localhost:9092",
      "group.id" -> "CountryCounter",
      "key.serializer" -> "io.confluent.kafka.serializers.KafkaAvroSerializer",
      "value.serializer" -> "io.confluent.kafka.serializers.KafkaAvroSerializer",
      "schema.registry.url" -> "http://localhost:8081"

    )
    val schema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/test.avsc")).mkString)

    // Create avro generic record object
    val genericUser: GenericRecord = new GenericData.Record(schema)

    //Put data in that generic record
    genericUser.put("id", 1)
    genericUser.put("name", "test_name")
    genericUser.put("email", null)

    val producer = new KafkaProducer[Int, GenericRecord](props.asJava)

    val record = new ProducerRecord("test", 1, genericUser)

    producer.send(record, new Callback() {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {


      }
    })

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

}

package helper

import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import scala.collection.JavaConverters._

class KafkaProducerWrapper(topicName: String , key : Int , record: GenericRecord) {

  private val producer = new KafkaProducer[Int, GenericRecord](KafkaProducerWrapper.props.asJava)

  private val producerRecord = new ProducerRecord(topicName, key, record)


  def send() : Unit = {

    producer.send(producerRecord, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        if (exception == null) {

          //accessLogger.error(exception.printStackTrace().toString)
        }

      }
    })
  }

  def stopProducer(): Unit = {
    producer.close()
  }


}

object KafkaProducerWrapper {

  val props: Map[String, AnyRef] = Map(
    "bootstrap.servers" -> "localhost:9092",
    "group.id" -> "CountryCounter",
    "key.serializer" -> "io.confluent.kafka.serializers.KafkaAvroSerializer",
    "value.serializer" -> "io.confluent.kafka.serializers.KafkaAvroSerializer",
    "schema.registry.url" -> "http://localhost:8081"
  )

}

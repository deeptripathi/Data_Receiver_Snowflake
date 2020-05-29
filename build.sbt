name := "Data_Receiver_Snowflake"
version := "1.0"
scalaVersion := "2.12.2"

lazy val `Data_Receiver_Snowflake` = (project in file(".")).enablePlugins(PlayScala)

resolvers ++= Seq( "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
 "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/" ,
"Confluent" at "http://packages.confluent.io/maven/" )

libraryDependencies ++= Seq( ws, javaWs, jdbc , ehcache , specs2 % Test , guice
, "com.typesafe.play" %% "play-json" % "2.8.1"
, "org.apache.avro" % "avro" % "1.7.7"
, "org.apache.kafka" %% "kafka" % "2.1.0"
 // https://mvnrepository.com/artifact/javax.ws.rs/javax.ws.rs-api
, "javax.ws.rs" % "jsr311-api" % "0.10"
 // https://mvnrepository.com/artifact/io.confluent/kafka-avro-serializer
, "io.confluent" % "kafka-avro-serializer" % "3.3.1"
 // https://mvnrepository.com/artifact/net.liftweb/lift-json
, "net.liftweb" %% "lift-json" % "3.4.1"
)

//unmanagedResourceDirectories in Test +=  baseDirectory ( _ /"target/web/public/test" )

      
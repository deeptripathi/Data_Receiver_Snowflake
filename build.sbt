name := "Data_Receiver_Snowflake"
 
version := "1.0" 
      
lazy val `Data_Receiver_Snowflake` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

resolvers += "Confluent" at "http://packages.confluent.io/maven/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( ws, javaWs, jdbc , ehcache , specs2 % Test , guice )
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.1"
// https://mvnrepository.com/artifact/javax.ws.rs/javax.ws.rs-api
libraryDependencies += "javax.ws.rs" % "jsr311-api" % "0.10"
libraryDependencies += "org.apache.avro" % "avro" % "1.7.7"
libraryDependencies += "org.apache.kafka" %% "kafka" % "2.1.0"
// https://mvnrepository.com/artifact/io.confluent/kafka-avro-serializer
libraryDependencies += "io.confluent" % "kafka-avro-serializer" % "3.3.1"






unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

      
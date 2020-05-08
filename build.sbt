name := "Data_Receiver_Snowflake"
 
version := "1.0" 
      
lazy val `Data_Receiver_Snowflake` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( ws, javaWs, jdbc , ehcache , specs2 % Test , guice )
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.1"
// https://mvnrepository.com/artifact/javax.ws.rs/javax.ws.rs-api
libraryDependencies += "javax.ws.rs" % "jsr311-api" % "0.10"




unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

      
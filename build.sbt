name := "gpxglitch"
 
version := "0.1"
      
lazy val `gpxglitch` = (project in file(".")).enablePlugins(PlayJava)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
scalaVersion := "2.11.11"

libraryDependencies ++= Seq( javaJdbc , cache , javaWs )
libraryDependencies += "io.jenetics" % "jpx" % "1.1.3"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.10.0"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.10.0"

unmanagedResourceDirectories in Test +=  baseDirectory.value / "target/web/public/test"

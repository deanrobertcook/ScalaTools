name := "Scala Tools"
version := "0.1"
scalaVersion := "2.11.4"


libraryDependencies ++= Seq(
  "com.github.tototoshi" %% "scala-csv" % "1.3.4",
  "org.scalactic" %% "scalactic" % "3.0.1",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",

  //tests
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

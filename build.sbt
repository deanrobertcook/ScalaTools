name := "Scala Tools"
version := "0.1"
scalaVersion := "2.11.4"


libraryDependencies ++= Seq(
  "com.github.tototoshi" %% "scala-csv" % "1.3.4",
  "org.scalactic" %% "scalactic" % "3.0.1",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",

  //TODO get this 3D charting up and running http://www.jzy3d.org/download-1.0.0.php
//  "org.jzy3d" % "jzy3d-api" % "1.0.0",

  //tests
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

//addZipJar("org.jzy3d" % "jzy3d-deps" % "0.9" from "http://www.jzy3d.org/release/0.9a3/org.jzy3d-0.9-dependencies.zip", Compile)

//{
//  val arch = "macosx" // "windows-amd64" "windows-i586" "linux-amd64" "linux-i586"
//  addZipJar("org.jzy3d" % "jzy3d-native" % "0.9" from s"http://www.jzy3d.org/release/0.9a3/org.jzy3d-0.9-binaries-$arch.zip", Compile)
//}
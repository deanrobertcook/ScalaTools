package org.theronin.problems

import java.io.File

import com.github.tototoshi.csv.CSVWriter
import com.typesafe.scalalogging.LazyLogging
import org.scalactic.NormMethods._
import org.scalactic.StringNormalizations._
import org.theronin.scalatools.csv.Column.{BoolColumn, StrColumn}
import org.theronin.scalatools.openCSVFromResource

object Titanic extends LazyLogging {

  import logger._

  implicit val strNormalization = lowerCased and trimmed

  val trainingCsv  = "kaggle-titanic/train.csv"
  val testCsv      = "kaggle-titanic/test.csv"
  val outputGender = "output/gendermodel.csv"


  val inputTrain = openCSVFromResource(trainingCsv).all()
  val inputTest  = openCSVFromResource(testCsv).all()


  def run(): Unit = {
    debug("Titanic running!")
    debug(s"Header: ${inputTest.head.mkString(", ")}")


    //training
    val numPassengers = inputTrain.tail.size
    val numSurvived = BoolColumn("survived", inputTrain).list.flatten.count(_ == true)
    val numWomen = StrColumn("sex", inputTrain).list.flatten.count(_.norm == "female")

    val ratioWomen = numWomen.toDouble / numSurvived.toDouble

    debug(s"numPassengers: $numPassengers, numSurvived: $numSurvived, numWomen: $numWomen, ratioWomen: $ratioWomen")

    val writer = CSVWriter.open(new File(s"src/main/resources/$outputGender"))

    //test
    val testHeader = inputTest.head
    inputTest.zipWithIndex.foreach {

      case (_, ind) if ind == 0 =>
        writer.writeRow(Seq("PassengerId", "Survived"))
      case (r, _) =>
        val pId = r(testHeader.indexOf("PassengerId"))
        val gender = r(testHeader.indexOf("Sex"))
        writer.writeRow(Seq(pId, if (gender == "female") "1" else "0"))
    }

    writer.flush()
    writer.close()
  }

}

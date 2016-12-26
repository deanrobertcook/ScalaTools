package org.theronin.scalatools.csv


import org.scalactic.NormMethods._
import org.scalactic.StringNormalizations._

trait Column[T] {
  val name: String
  val list: List[Option[T]]
}

object Column {

  implicit val strNormalization = lowerCased and trimmed

  object StrColumn {
    def apply(name: String, data: List[List[String]]): Column[String] = buildColumn[String](name, data) {
      case v if v.isEmpty => Option.empty[String]
      case v => Some(v)
    }
  }

  object IntColumn {
    def apply(name: String, data: List[List[String]]): Column[Int] = buildColumn[Int](name, data) {
      case v if v.isEmpty => Option.empty[Int]
      case v => Some(v.toInt)
    }
  }

  object DoubleColumn {
    def apply(name: String, data: List[List[String]]): Column[Double] = buildColumn[Double](name, data) {
      case v if v.isEmpty => Option.empty[Double]
      case v => Some(v.toDouble)
    }
  }

  object BoolColumn {
    def apply(name: String, data: List[List[String]]): Column[Boolean] = buildColumn[Boolean](name, data) {
      case v if v.isEmpty => Option.empty[Boolean]
      case v if v == "1" | v == "0" => Some(v == "1")
      case v => Some(v.toBoolean)
    }
  }

  private def buildColumn[T](columnName: String, data: List[List[String]])(f: String => Option[T]): Column[T] = {
    new Column[T] {
      override val name = columnName
      override val list = {
        val in = colInd(columnName, data)
        data.tail.map(v => f(v(in)))
      }
    }
  }

  private def colInd(name: String, data: List[List[String]]) = {
    val header = data.head.map(_.norm)
    val col = header.indexOf(name.norm)
    if (col == -1) throw new IllegalArgumentException(s"Undefined column named: $name")
    col
  }

}

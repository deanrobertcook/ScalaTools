package org.theronin.scalatools.csv


import org.scalactic.NormMethods._
import org.scalactic.StringNormalizations._

trait Column[T] {
  val list: List[Option[T]]
}

trait ColumnBuilder[T] {
  def filter(cell: String): Option[T]

  def apply(columnName: String, data: List[List[String]]): Column[T] = buildColumn(columnName, data)(filter)

  def apply(index: Int, data: List[List[String]]): Column[T] = buildColumn(index, data)(filter)

  private def buildColumn(columnName: String, data: List[List[String]])(f: String => Option[T]): Column[T] = {
    new Column[T] {
      override val list = {
        val in = colInd(columnName, data)
        data.tail.map(v => f(v(in)))
      }
    }
  }

  private def buildColumn(index: Int, data: List[List[String]])(f: String => Option[T]): Column[T] = {
    new Column[T] {
      override val list = data.map(v => f(v(index)))
    }
  }

  private def colInd(name: String, data: List[List[String]]) = {
    implicit val strNormalization = lowerCased and trimmed
    val header = data.head.map(_.norm)
    val col = header.indexOf(name.norm)
    if (col == -1) throw new IllegalArgumentException(s"Undefined column named: $name")
    col
  }

}

object Column {

  object StrColumn extends ColumnBuilder[String] {
    def filter(cell: String) = cell match {
      case v if v.isEmpty => Option.empty[String]
      case v => Some(v)
    }
  }

  object IntColumn extends ColumnBuilder[Int] {
    def filter(cell: String) = cell match {
      case v if v.isEmpty => Option.empty[Int]
      case v => Some(v.trim.toInt)
    }

  }

  object DoubleColumn extends ColumnBuilder[Double] {
    def filter(cell: String) = cell match {
      case v if v.isEmpty => Option.empty[Double]
      case v => Some(v.trim.toDouble)
    }
  }

  object BoolColumn extends ColumnBuilder[Boolean] {
    def filter(cell: String) = cell match {
      case v if v.isEmpty => Option.empty[Boolean]
      case v if v == "1" | v == "0" => Some(v == "1")
      case v => Some(v.trim.toBoolean)
    }
  }

}

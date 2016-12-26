package org.theronin.scalatools.csv

import org.scalatest.{FeatureSpec, Matchers}
import org.theronin.scalatools.csv.Column.{BoolColumn, DoubleColumn, IntColumn, StrColumn}

class ColumnSpec extends FeatureSpec with Matchers {

  feature("CSV file with headers") {
    lazy val data = openCSVFromResource("csv_with_headers.csv").all()
    scenario("sizes") {
      Seq(
        IntColumn("int", data),
        StrColumn("string", data),
        DoubleColumn("double", data),
        BoolColumn("boolean", data)
      ).foreach(_.list should have size 3)
    }
  }

  feature("CSV without headers") {
    lazy val data = openCSVFromResource("csv_without_headers.csv").all()
    scenario("sizes") {
      Seq(
        IntColumn(0, data),
        StrColumn(1, data),
        DoubleColumn(2, data),
        BoolColumn(3, data)
      ).foreach(_.list should have size 3)
    }
  }

}

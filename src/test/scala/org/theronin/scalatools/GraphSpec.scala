package org.theronin.scalatools

import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.chart.{NumberAxis, ScatterChart, XYChart}
import javafx.scene.control.Label
import javafx.scene.text.Font
import javafx.stage.Stage

import org.scalatest.FeatureSpec
import org.theronin.scalatools.graph.JavaFXRunner

class GraphSpec extends FeatureSpec {

  scenario("Draw a graph") {
    val application = new Application() {

      def start(primaryStage: Stage) {
        val xAxis = new NumberAxis("x", -5, 5, 1)
        val yAxis = new NumberAxis("y", -5, 5, 1)
        val chart = new ScatterChart[Number, Number](xAxis, yAxis)
        val series = new XYChart.Series[Number, Number]
        series.setName("A parabola")
        (-4.0 to 4.0 by 0.1).foreach { i =>
          val dataPoint = new XYChart.Data[Number, Number](i, i * i)

          val label = new Label(s"point: ${i.toString.take(3)}")
          label.setFont(new Font(10))

          if (i > 0) label.setStyle("-fx-background-color: blue;")
          else label.setStyle("-fx-background-color: red;")

          label.setPadding(new Insets(2, 4, 2, 4))
          dataPoint.setNode(label)
          series.getData.add(dataPoint)
        }

        chart.getData.add(series)
        primaryStage.setScene(new Scene(chart, 1000, 1000))
        primaryStage.show()
      }
    }

    JavaFXRunner(application).showGraphBlocking()
  }

}

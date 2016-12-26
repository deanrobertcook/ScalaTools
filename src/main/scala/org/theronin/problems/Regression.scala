package org.theronin.problems

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.chart.{NumberAxis, ScatterChart, XYChart}
import javafx.stage.Stage

import com.typesafe.scalalogging.LazyLogging
import org.theronin.scalatools.csv.Column.DoubleColumn
import org.theronin.scalatools.csv.openCSVFromResource
import org.theronin.scalatools.graph.JavaFXRunner

object Regression extends LazyLogging {

  import logger._


  def linearRegression(): Unit = {

    val data = openCSVFromResource("coursera-week2/ex1data1.txt").all()
    val population = DoubleColumn(0, data).list.flatten
    val profit = DoubleColumn(1, data).list.flatten

    debug(s"Population: $population\nprofit: $profit")
    plotData(population, profit)
  }


  def plotData(pop: List[Double], profit: List[Double]): Unit = {

    def createAxis(name: String, data: List[Double]) = {
      val (min, max) = (data.min, data.max)
      val tick = Math.round((max - min) / 10)
      //TODO - might want to ensure that (0, 0) is always in view to correctly represent stuff
      val (lb, ub) = (Math.round(min - tick * 2), Math.round(max + tick * 2))
      new NumberAxis(name, lb, ub, tick)
    }

    val application = new Application() {

      def start(primaryStage: Stage) {
        val xAxis = createAxis("population", pop)
        val yAxis = createAxis("profit", profit)
        val chart = new ScatterChart[Number, Number](xAxis, yAxis)
        val series = new XYChart.Series[Number, Number]
        series.setName("Food truck profit as a function of city population")

        pop.zip(profit).foreach {
          case (po, pr) =>
            val dataPoint = new XYChart.Data[Number, Number](po, pr)
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

package org.theronin.problems

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.chart.{LineChart, NumberAxis, ScatterChart, XYChart}
import javafx.scene.layout.Pane
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

    //    debug(s"Population: $population\nprofit: $profit")
    //    plotData(population, profit)

    val m = data.length.toDouble
    def squareErrors(theta0: Double, theta1: Double) = {
      def hypothesis(x: Double) = theta0 + theta1 * x
      (1 / (2 * m)) * population.zip(profit).map {
        case (po, pr) => Math.pow(hypothesis(po) - pr, 2)
      }.sum
    }

    debug(s"Square errors once: ${squareErrors(0, 0)}")

    val x = population.map(1D -> _)

    val alpha = 0.01
    val maxIterations = 1500

    def gradientDecent(th0Init: Double = 0, th1Init: Double = 0, i: Int = 0): (Double, Double) = {
      if (i == maxIterations) (th0Init, th1Init)
      else {
        val th0 = th0Init - alpha * (1D / m) * x.zip(profit).map {
          case ((x0, x1), y) => (th0Init * x0 + th1Init * x1 - y) * x0
        }.sum

        val th1 = th1Init - alpha * (1D / m) * x.zip(profit).map {
          case ((x0, x1), y) => (th0Init * x0 + th1Init * x1 - y) * x1
        }.sum
        gradientDecent(th0, th1, i + 1)
      }
    }

    val (th0, th1) = gradientDecent()
    plotData(population, profit, th0, th1)
  }

  def plotData(pop: List[Double], profit: List[Double], th0: Double, th1: Double): Unit = {

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

        val chart = new LineChart[Number, Number](xAxis, yAxis)

        // points
        val series1 = new XYChart.Series[Number, Number]
        series1.setName("Food truck profit as a function of city population")
        pop.zip(profit).foreach {
          case (po, pr) =>
            val dataPoint = new XYChart.Data[Number, Number](po, pr)
            series1.getData.add(dataPoint)
        }

        //plot line
        val series2 = new XYChart.Series[Number, Number]
        series2.setName(s"Linear regression, th0: ${th0.toString.take(4)}, th1: ${th1.toString.take(4)}")
        Seq((pop.min, th0 + th1 * pop.min), (pop.max, th0 + th1 * pop.max))
          .map { case (x, y) => new XYChart.Data[Number, Number](x, y) }
          .foreach(series2.getData.add(_))

        chart.getData.add(series1)
        chart.getData.add(series2)

        val scene = new Scene(chart, 1000, 1000)
        scene.getStylesheets.add(getClass.getResource("/coursera-week2/chart.css").toExternalForm)
        primaryStage.setScene(scene)
        primaryStage.show()
      }
    }

    JavaFXRunner(application).showGraphBlocking()
  }


}

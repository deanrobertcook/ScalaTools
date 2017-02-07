package org.theronin.problems.coursera

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.chart.{LineChart, NumberAxis, XYChart}
import javafx.stage.Stage

import com.typesafe.scalalogging.LazyLogging
import org.theronin.scalatools.csv.Column.{DoubleColumn, IntColumn}
import org.theronin.scalatools.csv.openCSVFromResource
import org.theronin.scalatools.graph.JavaFXRunner

object Woche2 extends LazyLogging {

  import logger._

  def uebungEins(): Unit = {

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

    val rows = population.map(Seq(1D, _)) // adds the x0 value (always 1) to the data set

    val alpha = 0.01
    val maxIterations = 1500

    val ths = gradientDecent(rows, profit, alpha, maxIterations)
    debug(s"th0: ${ths(0)}, th1: ${ths(1)}")
    plotData(population, profit, ths(0), ths(1))
  }

  def gradientDecent(data: Seq[Seq[Double]], ys: Seq[Double], alpha: Double, maxIterations: Int): Seq[Double] = {
    val m = data.length.toDouble
    def iterativeStep(thInits: Seq[Double], i: Int = 0): Seq[Double] = {

      if (i % 50 == 0) debug(s"Current ths: $thInits")

      if (i == maxIterations) thInits
      else {
        def partialDeriv(j: Int) = {
          thInits(j) - alpha * (1D / m) * data.zip(ys).map {
            case (xs, y) => (thInits.zip(xs).map { case (th, x) => th * x }.sum - y) * xs(j)
          }.sum
        }
        iterativeStep(thInits.indices.map(partialDeriv), i + 1)
      }
    }

    //initialise all thetas to 0
    iterativeStep(data.head.indices.map(_ => 0D))

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

  def uebungZwei(): Unit = {
    val daten = openCSVFromResource("coursera-week2/ex1data2.txt").all()

    val hausGroessen = IntColumn(0, daten).list.flatten
    val zimmeranzahlen = IntColumn(1, daten).list.flatten
    val preiss = IntColumn(2, daten).list.flatten


    val rows = hausGroessen.zip(zimmeranzahlen).map { case (gr, zi) => Seq(1D, gr.toDouble, zi.toDouble) }

    val alpha = 0.000001
    val maxIterations = 30000

    val ths = gradientDecent(rows, preiss.map(_.toDouble), alpha, maxIterations)

    debug(s"Ergebnis: p = ${ths(0)} + ${ths(1)}.x1 + ${ths(2)}.x2")

  }


}

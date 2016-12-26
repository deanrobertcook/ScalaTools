package org.theronin.scalatools.graph

import java.util.concurrent.CountDownLatch
import javafx.application.Application
import javafx.embed.swing.JFXPanel
import javafx.stage.Stage

import com.sun.javafx.application.PlatformImpl
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.concurrent.duration._
import scala.util.control.NonFatal

case class JavaFXRunner(javaFXApplication: Application) extends LazyLogging {

  import logger._

  private val ex = ExecutionContext.Implicits.global

  val programComplete = Promise[Unit]()

  def showGraph(): Future[Unit] = {
    new JFXPanel(); // Initializes the JavaFx Platform
    PlatformImpl.addListener(new PlatformImpl.FinishListener() {
      def idle(implicitExit: Boolean) {
        debug(s"idle: implicitExit: $implicitExit")
        programComplete.success({})
      }
      def exitCalled(): Unit = {
        debug("exitCalled")
      }
    })
    PlatformImpl.runLater(new Runnable {
      override def run() = {
        try javaFXApplication.start(new Stage())
        catch {
          case NonFatal(e) => logger.error("Something went wrong", e)
        }
      }
    })
    programComplete.future
  }

  def showGraphBlocking() = Await.ready(showGraph(), 10.minutes)

}

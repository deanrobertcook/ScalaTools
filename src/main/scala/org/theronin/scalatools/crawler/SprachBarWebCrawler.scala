package org.theronin.scalatools.crawler

import java.io.{File, PrintWriter}
import java.net.URL

import com.typesafe.scalalogging.LazyLogging
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.scalactic.NormMethods._
import org.scalactic.StringNormalizations._

import scala.sys.process._
import scala.util.Try

class SprachBarWebCrawler extends LazyLogging {

  implicit val strNormalization = lowerCased and trimmed

  import logger._

  import scala.io.Source

  val localDest = "/Users/deancook/Downloads"

  val baseUrl       = "http://www.dw.com"
  val sprachbarHome = s"$baseUrl/de/deutsch-lernen/sprachbar/s-9011"

  val archivUrlPattern = """/de/sprachbar-archiv-[a-z]+/a\-[0-9]*""".r
  val alphaPattern     = "sprachbar-archiv-[a-z]+".r

  val browser = JsoupBrowser()

  val dryRun = false

  def createIndexes() = {
    archivUrlPattern.findAllMatchIn(Source.fromURL(sprachbarHome).mkString).toSeq.map(_.toString).foreach { l =>
      alphaPattern.findFirstIn(l).foreach { n =>

        val dir = new File(s"$localDest/$n")
        if (!dir.exists()) dir.mkdir()

        val indexFile = new File(s"${dir.getAbsolutePath}/index.txt")

        val writer = new PrintWriter(indexFile)
        writer.write(getPodcasts(l).flatMap(findMedia).map(_.toString).mkString("\n"))
        writer.close()
      }
    }
  }

  def loadDirectoryNames() = {
    val base = new File(localDest)
    if (base.exists && base.isDirectory) {
      base.listFiles.filter(d => d.isDirectory && d.getName.contains("sprachbar-archiv"))
    }.toList
    else List[File]()
  }

  def loadFromIndexes() = {

    loadDirectoryNames().drop(1).foreach { dir =>
      Source.fromFile(s"${dir.getAbsolutePath}/index.txt").getLines().map(Media(_)).foreach {
        case Media(title, _, Some(link)) => downloadFile(link, s"${dir.getAbsolutePath}/$title.mp3")
        case Media(title, _, None) => debug(s"No link present for: $title")
      }
    }
  }

  def downloadFile(url: String, dest: String) = {
    if (dryRun) debug(s"Would have downloaded from $url and saved to: $dest")
    else {
      debug(s"Downloading: $url to $dest")
      new URL(url) #> new File(dest) !!
    }
  }

  def getPodcasts(archivLink: String) = {
    val doc = browser.get(s"$baseUrl$archivLink")
    (doc >> elementList(".linkList")).map(_ >> attr("href")("a"))
  }

  def findMedia(podcastLink: String) = {
    val doc = browser.get(s"$baseUrl$podcastLink")

    val title = (doc >?> element("h1")).map(_.text)
    debug(s"Found title: $title")


    val (link, date) = Try {
      (doc >?> element(".overlayIcon") >> attr("href")("a")).map(l => browser.get(s"$baseUrl$l")).map { overlayDoc =>

        val downloadLink = (overlayDoc >> attrs("href")("a")).find(_.contains("radio-download"))

        val date = (overlayDoc >> elementList("li")).flatMap { e =>
          val st = (e >?> element("strong")).map(_.text.norm == "datum")
          st flatMap {
            case true => Some(e.text.replace("Datum", "").trim)
            case _ => None
          }
        }.headOption
        (downloadLink, date)
      }.getOrElse((None, None))
    }.toOption.getOrElse(None, None)


    (title, date, link) match {
      case (Some(t), d, l) => Some(Media(t, d, l))
      case _ => None
    }
  }

  case class Media(title: String, date: Option[String], link: Option[String]) {

    import Media._

    override def toString = s"$title${date.map(d => s"$sep$d").getOrElse("")}${link.map(l => s"$sep$l").getOrElse("")}"
  }

  object Media {
    val sep = "|" //don't use commas - titles have them

    def apply(str: String): Media = {
      //careful of regex overlapping seperators - use char instead of str
      val parts = str.split(sep.charAt(0))

      parts.length match {
        case 3 => Media(parts(0), Some(parts(1)), Some(parts(2)))
        case 2 => Media(parts(0), Some(parts(1)), None)
        case 1 => Media(parts(0), None, None)
        case _ => throw new IllegalArgumentException(s"Unexpected number of index parts: ${parts.length}")
      }
    }
  }

}

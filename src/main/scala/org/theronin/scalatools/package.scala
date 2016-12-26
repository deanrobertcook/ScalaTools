package org.theronin

import java.io.File

import com.github.tototoshi.csv.CSVReader
import org.theronin.problems.Titanic.getClass

/**
  * Created by deancook on 26/12/16.
  */
package object scalatools {

  //No need for preceding slash
  def openCSVFromResource(resourceName: String) =
    CSVReader.open(new File(getClass.getResource(s"/$resourceName").toURI))
}

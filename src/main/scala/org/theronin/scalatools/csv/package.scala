package org.theronin.scalatools

import java.io.File

import com.github.tototoshi.csv.CSVReader

/**
  * Created by deancook on 26/12/16.
  */
package object csv {

  //No need for preceding slash
  def openCSVFromResource(resourceName: String) =
    CSVReader.open(new File(getClass.getResource(s"/$resourceName").toURI))
}

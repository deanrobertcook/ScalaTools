package org.theronin.problems

import org.scalatest.FeatureSpec
import org.theronin.problems.coursera.Woche2

class RegressionSpec extends FeatureSpec {

  scenario("Run linear regression") {
    Woche2.uebungEins()
  }

  scenario("Lineare Regression mit mehrere unhabaengige Variablen") {
    Woche2.uebungZwei()
  }

}

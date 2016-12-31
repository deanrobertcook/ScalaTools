package org.theronin.scalatools.crawler

import org.scalatest.FeatureSpec

class SprachBarWebCrawlerSpec extends FeatureSpec {

  scenario("Download indicies") {
    (new SprachBarWebCrawler).createIndexes()
  }

  scenario("Load mp3 data from indicies") {
    (new SprachBarWebCrawler).loadFromIndexes()
  }

}

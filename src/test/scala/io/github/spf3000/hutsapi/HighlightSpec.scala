package com.elsevier.entellect.xml

import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification

import scala.xml._
import Highlighter._

class HighlighterSpec extends Specification {

//                   0         1         2         3         4
//                   012345678901234567890123456789012345678901
//val gold = Golden("<week><day>Mon</day><day>Tues</day></week>")
//                   000000000000123333333333334567777777777777
//                  "<week><day>Mon</day></week>"

  "Highlighter should" >> {
    "insert a tag" >> {
      highlightAll("<week><day>Mon</day><day>Tues</day></week>",
        Seq(
          OTag("sax",11,13,""),
          OTag("Aspirin",26,28,""))
          , "span") must beEqualTo(
            "<week><day><span>Mo</span>n</day><day>T<span>ue</span>s</day></week>"

        )



    }


  }


}

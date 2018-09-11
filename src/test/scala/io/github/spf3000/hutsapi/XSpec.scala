package io.github.spf3000.hutsapi

import matryoshka._
import matryoshka.implicits._
import scala.xml._
import matryoshka.data._

import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification

import scalaz._
import Scalaz._
import X._

class XSpec extends Specification {


  "X should do something" >> {
    val l = List(1,2,3).filter

    Fix[X](El(
      "week", IList(
        Fix[X](El("day",
          IList(Fix[X](El("Mon", IList())))))))).cataM(stateAlg).run(0) must beEqualTo ("hello")

  }

}

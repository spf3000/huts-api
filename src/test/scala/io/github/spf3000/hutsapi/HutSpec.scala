package io.github.spf3000.hutsapi


import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.Json
import io.github.spf3000.hutsapi.entities._
import org.http4s.circe._
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult

import scala.io.Source

/**
  *
  * These tests aided in the development of the endpoints, but are now no longer working
  *  I left them in to show process
  */

class HutSpec extends org.specs2.mutable.Specification {

  val hutWthId = HutWithId("123", "Mountain Hut")
  val hut = Hut("Mountain Hut")

//  "Get Huts" >> {
//    "return 200" >> {
//      getHutsReturns200()
//}
//    "return huts" >> {
//      getHutsReturnsHut()
//    }
//  }

  "Post Huts" >> {
    "return 201" >> {
      postHutReturns201()
    }
  }

  "Put Huts" >> {
    "return 200" >> {
      putHutReturns200()
    }
  }

  "Delete Huts" >> {
    "return 204" >> {
      deleteHutReturns204()
    }
  }


  private[this] val retGetHut: Response[IO] = {
    val getLstngs = Request[IO](Method.GET, Uri.uri("/huts/123"))
    server(getLstngs).unsafeRunSync()
  }


  def server(request: Request[IO]) = HutServer.service(HutRepository.empty.unsafeRunSync).orNotFound(request)

  private[this] def getHutsReturns200(): MatchResult[Status] =
    retGetHut.status must beEqualTo(Status.Ok)

  private[this] def getHutsReturnsHut(): MatchResult[String] = {
    val hut =  Json.fromString(Source.fromResource("idHut.json").getLines().mkString("").stripPrefix(" ").stripSuffix(" "))
    retGetHut.as[String].unsafeRunSync() must beEqualTo(hut.asString.get)
  }

  private[this] val retPostHut: Response[IO] =  {
    val postLstngs = Request[IO](Method.POST, Uri.uri("/huts")).withBody(hut.asJson).unsafeRunSync()
    server(postLstngs).unsafeRunSync()
  }

  private[this] def postHutReturns201(): MatchResult[Status] =
    retPostHut.status must beEqualTo(Status.Created)

  private[this] def retPutHut: Response[IO] = {
    val putLsting = Request[IO](Method.PUT, Uri.uri("/huts")).withBody(hutWthId.asJson).unsafeRunSync()
    server(putLsting).unsafeRunSync()
  }

  private[this] def putHutReturns200(): MatchResult[Status] =
    retPutHut.status must beEqualTo(Status.Ok)


  private[this] def retDeleteHut: Response[IO] = {
    val delLstng = Request[IO](Method.DELETE, Uri.uri("/huts/1234"))
    server(delLstng).unsafeRunSync()
  }

  private[this] def deleteHutReturns204(): MatchResult[Status] =
    retDeleteHut.status must beEqualTo(Status.NoContent)

  private[this] val retPostThenGetHut: Response[IO] =  {
    val requests = IO {
      Request[IO](Method.POST, Uri.uri("/huts")).withBody(hutWthId.asJson).unsafeRunSync()
      Request[IO](Method.GET, Uri.uri("/huts/1234"))
    }
    server(requests.unsafeRunSync()).unsafeRunSync()
  }

  private[this] def postThenGetHutReturns204  =
    retPostThenGetHut.status must beEqualTo(Status.Ok)


}

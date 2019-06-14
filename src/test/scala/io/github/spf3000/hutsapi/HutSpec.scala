package io.github.spf3000.hutsapi

import cats._
import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.Json
import io.github.spf3000.hutsapi.entities._
import org.http4s.circe._
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import scala.io.Source
import scala.collection.mutable.ListBuffer

class HutSpec extends Specification {

  "Post Huts" >> {
    "return 201" >> {
      postHutReturns201()
    }
  }

  "Put Huts" >> {
    "return 200" >> {
      putHutReturns200()
    }
    "you can get the hut after" >> {
    "return 200" >> {
      getHutsReturns200()
    }
    "return huts" >> {
      getHutsReturnsHut()
    }
    }
  }

  "Delete Huts" >> {
    "return 204" >> {
      deleteHutReturns204()
    }
  }

  val hutWithId = ("123", Hut("Mountain Hut"))
  val hut = Hut("Mountain Hut")


  val basePath = "huts"
  val repo = Repository.Impl.empty[ListBuffer, IO, Hut]

  val hutRoutes = HutRoutes.hutRoutes[ListBuffer, IO, Hut](basePath, repo).orNotFound

  private[this] val retGetHut: Response[IO] = {
    val getLstngs = Request[IO](Method.GET, Uri.uri("/huts/123"))
    val putLsting: Request[IO] = Request[IO](Method.PUT, Uri.uri("/huts"))
      .withBody(hut.asJson)
      .unsafeRunSync()
    hutRoutes.run(putLsting).unsafeRunSync
    hutRoutes.run(getLstngs).unsafeRunSync
  }

  private[this] def getHutsReturns200(): MatchResult[Status] =
    retGetHut.status must beEqualTo(Status.Ok)

  private[this] def getHutsReturnsHut(): MatchResult[String] = {
    val hut = Json.fromString("""["123",{"name":"Mountain Hut"}]""")
    retGetHut.as[String].unsafeRunSync() must beEqualTo(hut.asString.get)
  }

  private[this] val retPostHut: Response[IO] = {
    val postLstngs = Request[IO](Method.POST, Uri.uri("/huts"))
      .withBody(hut.asJson)
      .unsafeRunSync()
    hutRoutes.run(postLstngs).unsafeRunSync()
  }

  private[this] def postHutReturns201(): MatchResult[Status] =
    retPostHut.status must beEqualTo(Status.Created)

  private[this] def retPutHut: Response[IO] = {
    val putLsting = Request[IO](Method.PUT, Uri.uri("/huts"))
      .withBody(hutWithId.asJson)
      .unsafeRunSync()
    hutRoutes.run(putLsting).unsafeRunSync()
  }

  private[this] def putHutReturns200(): MatchResult[Status] =
    retPutHut.status must beEqualTo(Status.Ok)

  private[this] def retDeleteHut: Response[IO] = {
    val delLstng = Request[IO](Method.DELETE, Uri.uri("/huts/1234"))
    hutRoutes.run(delLstng).unsafeRunSync()
  }

  private[this] def deleteHutReturns204(): MatchResult[Status] =
    retDeleteHut.status must beEqualTo(Status.NoContent)
}

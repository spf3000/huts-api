package io.github.spf3000.hutsapi


import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.Json
import io.github.spf3000.hutsapi.entities.{Address, Contact, Hut, Location}
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

  "Get Listings" >> {
    "return 200" >> {
      getListingsReturns200()
    }
    "return listings" >> {
      getListingsReturnsListing()
    }
  }

  "Post Listings" >> {
    "return 201" >> {
      postListingReturns201()
    }
    "return id" >> {
      postListingsReturnsListing()
    }
  }

  "Put Listings" >> {
    "return 204" >> {
      putListingReturns204()
    }
  }

  "Delete Listings" >> {
    "return 204" >> {
      deleteListingReturns204()
    }
  }


  private[this] val retGetListing: Response[IO] = {
    val getLstngs = Request[IO](Method.GET, Uri.uri("/listings/123"))
    HutServer.service.orNotFound(getLstngs).unsafeRunSync()
  }

  private[this] def getListingsReturns200(): MatchResult[Status] =
    retGetListing.status must beEqualTo(Status.Ok)

  private[this] def getListingsReturnsListing(): MatchResult[String] = {
    val listing =  Json.fromString(Source.fromResource("idListing.json").getLines().mkString("").stripPrefix(" ").stripSuffix(" "))
    retGetListing.as[String].unsafeRunSync() must beEqualTo(listing.asString.get)
  }

  private[this] val retPostListing: Response[IO] =  {
    val postLstngs = Request[IO](Method.POST, Uri.uri("/listings")).withBody(listing.asJson).unsafeRunSync()
    HutServer.service.orNotFound(postLstngs).unsafeRunSync()
  }

  private[this] def postListingReturns201(): MatchResult[Status] =
    retPostListing.status must beEqualTo(Status.Created)

  private[this] def postListingsReturnsListing(): MatchResult[String] = {
    val listing =  Json.fromString(Source.fromResource("idListing.json").getLines().mkString("").stripPrefix(" ").stripSuffix(" "))
    retPostListing.as[String].unsafeRunSync() must beEqualTo("""{"id":"1234"}""")
  }

  private[this] def retPutListing: Response[IO] = {
    val putLsting = Request[IO](Method.PUT, Uri.uri("/listings")).withBody(listing.asJson).unsafeRunSync()
    HutServer.service.orNotFound(putLsting).unsafeRunSync()
  }

  private[this] def putListingReturns204(): MatchResult[Status] =
    retPutListing.status must beEqualTo(Status.NoContent)


  private[this] def retDeleteListing: Response[IO] = {
    val delLstng = Request[IO](Method.DELETE, Uri.uri("/listings/1234"))
    HutServer.service.orNotFound(delLstng).unsafeRunSync()
  }

  private[this] def deleteListingReturns204(): MatchResult[Status] =
    retDeleteListing.status must beEqualTo(Status.NoContent)

  private[this] val retPostThenGetListing: Response[IO] =  {
    val requests = IO {
      Request[IO](Method.POST, Uri.uri("/listings")).withBody(listing.asJson).unsafeRunSync()
      Request[IO](Method.GET, Uri.uri("/listings/1234"))
    }
    HutServer.service.orNotFound(requests.unsafeRunSync()).unsafeRunSync()
  }

  private[this] def postThenGetListingReturns204  =
    retPostThenGetListing.status must beEqualTo(Status.Ok)


  private val contact = Contact("0123456789", "01234 567 89")
  private val address = Address("10 Downing Street", "SW1A 2AA", "UK", "London", "London", "United Kingdom")
  private val location = Location("40.4255485534668", "-3.7075681686401367")

  private val listing = Hut(contact, address, location)



}

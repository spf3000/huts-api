package io.github.spf3000.listingsapi

import cats.effect.IO
import fs2.StreamApp
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import entities.Listing

import entities._
import ListingRepository._

object ListingServer extends StreamApp[IO] with Http4sDsl[IO] {

  implicit val decoder = jsonOf[IO, Listing]

  implicit val decoder1 = jsonOf[IO, ListingWithId]

  implicit val endcoder = jsonEncoderOf[IO, ListingWithId]

  val service = HttpService[IO] {

    case GET -> Root / "listings" / listingId =>
      getListing(listingId)
          .flatMap(_.fold(NotFound())(Ok(_)))

    case req @ POST -> Root / "listings" =>
         req.as[Listing].flatMap(addListing).flatMap(Ok(_))

    case req @ PUT -> Root / "listings" / listingId =>
      req.as[ListingWithId]
        .flatMap(updateListing)
          .flatMap(_.fold(NotFound())(Ok(_)))

    case DELETE -> Root / "listings" / listingId =>
      deleteListing(listingId)
        .flatMap(_ => Ok())

  }

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve
}

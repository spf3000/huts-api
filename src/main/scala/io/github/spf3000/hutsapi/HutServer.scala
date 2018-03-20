package io.github.spf3000.hutsapi

import cats.effect.IO
import fs2.StreamApp
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import entities.Hut

import entities._
import HutRepository._

object HutServer extends StreamApp[IO] with Http4sDsl[IO] {

  implicit val decoder = jsonOf[IO, Hut]

  implicit val decoder1 = jsonOf[IO, HutWithId]

  implicit val endcoder = jsonEncoderOf[IO, HutWithId]

  val HUTS = "huts"

  val service = HttpService[IO] {

    case GET -> Root / HUTS / hutId =>
      getHut(hutId)
          .flatMap(_.fold(NotFound())(Ok(_)))

    case req @ POST -> Root / HUTS =>
         req.as[Hut].flatMap(addHut).flatMap(Ok(_))

    case req @ PUT -> Root / HUTS =>
      req.as[HutWithId]
        .flatMap(updateHut)
          .flatMap(_.fold(NotFound())(Ok(_)))

    case DELETE -> Root / HUTS / hutId =>
      deleteHut(hutId)
        .flatMap(_ => Ok())

  }

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve
}

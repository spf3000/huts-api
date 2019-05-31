package io.github.spf3000.hutsapi

import cats.effect._
import fs2.Stream

import org.http4s.server.blaze.BlazeBuilder

import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._


object HutServer {


  def stream[F[_]: ConcurrentEffect: Timer](hutRepo: HutRepository[F]) = {

    val httpApp = (HutRoutes.hutRoutes[F](hutRepo)).orNotFound

        for {
      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(httpApp)
        .serve
    } yield exitCode
  }.drain
}

package io.github.spf3000.hutsapi

import cats.effect._
import fs2.Stream
import scala.collection.mutable.ListBuffer

import org.http4s.server.blaze.BlazeBuilder

import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import io.circe.Encoder
import io.circe.Decoder


object HutServer {

//TODO make a trait instead of ListBuffer
  def stream[F[_]: ConcurrentEffect: Timer, A: Encoder: Decoder]
    (basePath: String, s: InMemoryStore[F,(String,A)]) = {
    val httpApp =
      (HutRoutes.hutRoutes[F,A](basePath, Repository.impl[F, A](s))).orNotFound
      for {
        exitCode <- BlazeServerBuilder[F]
          .bindHttp(8080, "0.0.0.0")
          .withHttpApp(httpApp)
          .serve
      } yield exitCode
    }.drain
}

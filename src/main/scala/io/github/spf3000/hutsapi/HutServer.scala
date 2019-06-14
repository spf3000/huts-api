package io.github.spf3000.hutsapi

import cats.effect._
import fs2.Stream
import scala.collection.mutable.ListBuffer

import org.http4s.server.blaze.BlazeBuilder

import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import io.circe.Encoder
import io.circe.Decoder
import cats.Monad

import InMemoryStore._

object HutServer {
  def stream[L[_],
             F[_]: ConcurrentEffect: Timer: Sync: Monad,
             A: Encoder: Decoder](basePath: String)(
      implicit S: InMemoryStore[L, F, (String, A)]) = {
        val R = Repository.Impl.empty[L,F,A]
    val httpApp =
      (HutRoutes.hutRoutes[L, F, A](basePath, R)).orNotFound
    for {
      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(httpApp)
        .serve
    } yield exitCode
  }.drain
}

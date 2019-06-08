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

//TODO make a trait instead of ListBuffer
  def stream[F[_]: ConcurrentEffect: Timer: Sync: Monad, A: Encoder: Decoder](basePath: String)
     = {
     implicit val store: LBStore[F,(String,A)] = InMemoryStore.lbImpl[F,(String,A)]
     val repo = Repository.impl[ListBuffer,F,A](store)
    val httpApp =
      (HutRoutes.hutRoutes[ListBuffer,F,A](basePath, repo)).orNotFound
      for {
        exitCode <- BlazeServerBuilder[F]
          .bindHttp(8080, "0.0.0.0")
          .withHttpApp(httpApp)
          .serve
      } yield exitCode
    }.drain
}

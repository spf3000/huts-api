package io.github.spf3000.hutsapi

import org.http4s.dsl.Http4sDsl
import cats.effect.Sync
import org.http4s.HttpRoutes
import cats.effect.Effect
import cats.implicits._
import entities._
import org.http4s._
import org.http4s.circe._
import io.circe.syntax._
import io.circe.Encoder
import io.circe.Decoder

object HutRoutes {

  def hutRoutes[L[_], F[_]: Sync, A: Encoder: Decoder]
  (basePath: String, R: Repository[L, F, A]) = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / basePath / id => {
        R.get(id)
          .flatMap(_ match {
            case Some(a) => Ok(a.asJson)
            case None    => NotFound()
          })
      }

      case req @ POST -> Root / basePath =>
        req
          .decodeJson[A]
          .flatMap(R.insert)
          .flatMap(Created(_))

      case req @ PUT -> Root / basePath =>
        req
          .decodeJson[(String, A)]
          .flatMap(R.update _)
          .map(_ => Response(status = Ok))

      case DELETE -> Root / basePath / id =>
        R.delete(_._1 == id)
          .map(_ => Response(status = NoContent))
    }
  }
}

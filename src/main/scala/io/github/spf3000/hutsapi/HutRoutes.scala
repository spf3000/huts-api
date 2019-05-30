package io.github.spf3000.hutsapi

import org.http4s.dsl.Http4sDsl
import cats.effect.Sync
import org.http4s.HttpRoutes
import cats.effect.Effect
import cats.implicits._
import entities._
import org.http4s._
import org.http4s.circe._
import io.circe.generic.auto._
import io.circe.syntax._

object HutRoutes {

  val hutsPath = "huts"

  def hutRoutes[F[_]](hutRepo: HutRepository[F])
    (implicit F: Sync[F]) = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / hutsPath / hutId => {
        hutRepo.getHut(hutId)
          .flatMap(_ match {
            case Some(hut) => Ok(hut.asJson)
            case None      => NotFound(hutId)
          })
        }

      case req @ POST -> Root / hutsPath =>
        req
          .decodeJson[Hut]
          .flatMap(hutRepo.addHut)
          .flatMap(hut => Created(hut))

      case req @ PUT -> Root / hutsPath =>
        req
          .decodeJson[HutWithId]
          .flatMap(hutRepo.updateHut)
          .map(_ => Response(status = Ok))

      case DELETE -> Root / hutsPath / hutId =>
        hutRepo
          .deleteHut(hutId)
          .map(_ => Response(status = NoContent))
    }
  }
}

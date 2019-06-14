package io.github.spf3000.hutsapi.entities

import io.circe.{Encoder, Decoder, Json, HCursor}
import io.circe.generic.semiauto._
import cats.effect.Sync
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe._

case class Hut(name: String)
object Hut{
implicit val hutDecoder: Decoder[Hut] = deriveDecoder[Hut]
implicit def hutEntityDecoder[F[_]: Sync]: EntityDecoder[F, Hut] =
        jsonOf
}

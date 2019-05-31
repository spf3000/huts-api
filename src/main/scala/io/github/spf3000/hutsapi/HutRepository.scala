package io.github.spf3000.hutsapi

import java.util.UUID
import cats.effect._
import scala.collection.mutable.ListBuffer
import cats.implicits._
import io.github.spf3000.hutsapi.entities._

final case class HutRepository[F[_]](private val huts: ListBuffer[HutWithId])(
    implicit e: Sync[F]
) {
  val makeId: F[String] = e.delay { UUID.randomUUID().toString }

  def getHut(id: String): F[Option[HutWithId]] =
    e.delay { huts.find(_.id == id) }

  def addHut(hut: Hut): F[String] =
    for {
      uuid <- makeId
      _ <- e.delay { huts += HutWithId(uuid, hut) }
    } yield uuid

  def updateHut(hutWithId: HutWithId): F[Unit] = {
    for {
      _ <- e.delay { huts -= hutWithId }
      _ <- e.delay { huts += hutWithId }
    } yield ()
  }

  def deleteHut(hutId: String): F[Unit] =
    e.delay { huts.find(_.id == hutId).foreach(h => huts -= h) }

}
object HutRepository {
  def empty[F[_]](implicit f: Sync[F]): F[HutRepository[F]] = f.delay {
    new HutRepository[F](ListBuffer())
  }
}

package io.github.spf3000.hutsapi

import java.util.UUID

import scala.collection.mutable.ListBuffer

import cats.effect.IO
import io.github.spf3000.hutsapi.entities._


final class HutRepository(private val huts: ListBuffer[HutWithId]) {
  val makeId: IO[String] = IO { UUID.randomUUID().toString }

    def getHut(id: String): IO[Option[HutWithId]] =
      IO  { huts.find(_.id == id) }

    def addHut(hut: Hut): IO[String] =
      for {
        uuid <- makeId
        _ <- IO { huts += hutWithId(hut, uuid) }
      } yield uuid

    def updateHut(hutWithId: HutWithId): IO[Unit] = {
      for {
        _ <- IO { huts -= hutWithId }
        _ <- IO { huts += hutWithId }
      } yield()
    }

    def deleteHut(hutId: String): IO[Unit] =
      IO {huts.filterNot(_.id == hutId)}

    def hutWithId(hut: Hut, id: String): HutWithId =
      HutWithId(id, hut.name)
}
object HutRepository {
  def empty: IO[HutRepository] = IO{new HutRepository(ListBuffer())}
}

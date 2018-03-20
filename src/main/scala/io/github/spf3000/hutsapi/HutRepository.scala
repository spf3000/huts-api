package io.github.spf3000.hutsapi

import java.util.UUID

import cats.effect.IO
import io.github.spf3000.hutsapi.entities._

import scala.collection.mutable.ListBuffer

  object HutRepository {

    private val mountainHut = HutWithId("123", "Mountain Hut")
    val huts = ListBuffer[HutWithId](mountainHut)
    val makeId: IO[String] = IO { UUID.randomUUID().toString }

    def getHut(listingId: String): IO[Option[HutWithId]] =
      IO  { huts.find(_.id == listingId) }

    def addHut(listing: Hut): IO[String] =
      for {
        uuid <- makeId
        _ <- IO { huts += hutWithId(listing, uuid) }
      } yield uuid

    def updateHut(listingWithId: HutWithId): IO[Option[HutWithId]] =
      for {
        hut <- IO { huts.find(_.id == listingWithId.id) }
        _ <- IO { hut.map(h => huts -= h )}
      } yield hut

    def deleteHut(listingId: String): IO[Unit] =
      IO {huts.filterNot(_.id == listingId)}

    def hutWithId(hut: Hut, id: String): HutWithId =
      HutWithId(id, hut.name)
  }




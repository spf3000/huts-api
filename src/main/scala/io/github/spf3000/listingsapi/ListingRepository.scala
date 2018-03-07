package io.github.spf3000.listingsapi

import java.util.UUID

import cats.effect.IO
import io.github.spf3000.listingsapi.entities._

import scala.collection.mutable.ListBuffer

  object ListingRepository {

    private val contact = Contact("0123456789", "01234 567 89")
    private val address = Address("10 Downing Street", "SW1A 2AA", "UK", "London", "London", "United Kingdom")
    private val location = Location("40.4255485534668", "-3.7075681686401367")

    private val listing = ListingWithId("123", contact, address, location)

    val listings = ListBuffer[ListingWithId](listing)

    val makeId: IO[String] = IO { UUID.randomUUID().toString }

    def getListing(listingId: String): IO[Option[ListingWithId]] =
      IO { listings.find(_.id == listingId) }

    def addListing(listing: Listing): IO[String] =
      for {
        uuid <- makeId
        _ <- IO { listings += listingWithId(listing, uuid) }
      } yield uuid

    def updateListing(listingWithId: ListingWithId): IO[Option[ListingWithId]] =
      for {
        listing <- IO { listings.find(_.id == listingWithId.id) }
        _ <- IO { listing.map(l => listings -= l )}
      } yield listing

    def deleteListing(listingId: String): IO[Unit] =
      IO {listings.filterNot(_.id == listingId)}

    def listingWithId(listing: Listing, id: String): ListingWithId =
      ListingWithId(id, listing.contact, listing.address, listing.location)
  }




package io.github.spf3000.listingsapi.entities


case class Listing(contact: Contact, address: Address, location: Location)

case class ListingWithId(id: String, contact: Contact, address: Address, location: Location)

case class Contact(phone: String, formattedPhone: String)

case class Address(address: String, postalCode: String, countryCode: String, city: String, state: String, country: String)

case class Location(lat: String, lng: String)


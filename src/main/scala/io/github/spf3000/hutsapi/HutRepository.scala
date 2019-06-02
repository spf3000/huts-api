package io.github.spf3000.hutsapi

import java.util.UUID
import cats.effect._
import scala.collection.mutable.ListBuffer
import cats.implicits._
import io.github.spf3000.hutsapi.entities._

trait InMemoryStore[F[_], A] {
  def add(a: A): F[Unit]
  def remove(a: A): F[Unit]
  def find(f: A => Boolean): F[Option[A]]
}

object InMemoryStore {
  object LBStore {
  def impl[F[_], A](lb: ListBuffer[A])(implicit F: Sync[F]): InMemoryStore[F,A] = new InMemoryStore[F,A] {
  def add(a: A): F[Unit] = F.delay{lb += a}
  def remove(a: A): F[Unit] = F.delay{lb -= a}
  def find(f: A => Boolean): F[Option[A]] = F.delay{ lb.find(f)}
  }
  def empty[F[_],A](implicit F: Sync[F]): F[InMemoryStore[F,A]] = F.delay(impl(new ListBuffer[A]))
  }
}


trait Repository[F[_], A] {
  type IdA = (String, A)
  def get(id: String): F[Option[IdA]]
  def insert(a: A): F[String]
  def makeId: F[String]
  def update(a: IdA): F[Unit]
  def delete(f: IdA => Boolean): F[Unit]
}
object Repository {

  def impl[F[_], A](S: InMemoryStore[F,(String,A)])(implicit F: Sync[F]): Repository[F, A] = new Repository[F, A] {
    def get(id: String): F[Option[IdA]] = S.find(_._1 == id)
    def insert(a: A): F[String] =
      for {
        uuid <- makeId
        idA = (uuid, a)
        _    <- S.add(idA)
      } yield uuid

    val makeId: F[String] = F.delay { UUID.randomUUID().toString }
    def update(idA: IdA): F[Unit] =
      for {
        _ <- S remove idA
        _ <- S add idA
      } yield ()
    def delete(f: IdA => Boolean): F[Unit] =
       S.find(f)
         .map(_.foreach(S.remove))
  }

}

package io.github.spf3000.hutsapi

import java.util.UUID
import cats.effect._
import scala.collection.mutable.ListBuffer
import cats.implicits._
import io.github.spf3000.hutsapi.entities._

trait InMemoryStore[L[_],F[_],A] {
  def add(a: A): F[Unit]
  def remove(a: A): F[Unit]
  def find(f: A => Boolean): F[Option[A]]
}
object InMemoryStore {
  type LBStore[F[_],A] = InMemoryStore[ListBuffer,F,A]
  implicit def lbImpl[F[_], A](implicit F: Sync[F]): LBStore[F,A] = new InMemoryStore[ListBuffer,F,A] {
    val lb = new ListBuffer[A]()
  def add(a: A): F[Unit] = F.delay(lb += a)
  def remove(a: A): F[Unit] = F.delay(lb -= a)
  def find(f: A => Boolean): F[Option[A]] = F.delay(lb.find(f))
  }

}

  class ListBufferInMemoryStore[F[_],A](L: ListBuffer[A])(implicit F: Sync[F]) extends InMemoryStore[ListBuffer,F,A] {
  def add(a: A): F[Unit] = F.delay(L += a)
  def remove(a: A): F[Unit] = F.delay(L -= a)
  def find(f: A => Boolean): F[Option[A]] = F.delay(L.find(f))
  }
  object ListBufferInMemoryStore {
    def empty[F[_],A](implicit F: Sync[F]):F[ListBufferInMemoryStore[F,A]] = F.delay(new ListBuffer[A]()).map(lb => new ListBufferInMemoryStore(lb))
  }

  trait Random[F[_]] {
    def randomUUID: F[String]
  }

  object Random {
    implicit def syncRandom[F[_]](implicit F: Sync[F]) = new Random[F] {
      def randomUUID = F.delay { UUID.randomUUID().toString }
    }
  }

trait Repository[L[_],F[_], A] {
  type IdA = (String, A)
  def get(id: String): F[Option[IdA]]
  def insert(a: A): F[String]
  def update(a: IdA): F[Unit]
  def delete(f: IdA => Boolean): F[Unit]
}
object Repository {

  def impl[L[_], F[_], A](S: InMemoryStore[L,F,(String,A)])(implicit F: Sync[F], R: Random[F]): Repository[L,F, A] = new Repository[L,F, A] {
    def get(id: String): F[Option[IdA]] = S.find(_._1 == id)
    def insert(a: A): F[String] =
      for {
        uuid <- R.randomUUID
        idA = (uuid, a)
        _    <- S.add(idA)
      } yield uuid
    def update(idA: IdA): F[Unit] =
      for {
        _ <- S remove idA
        _ <- S add idA
      } yield ()
    def delete(f: IdA => Boolean): F[Unit] =
      S.find(f).map(_.foreach(S.remove))
  }
}

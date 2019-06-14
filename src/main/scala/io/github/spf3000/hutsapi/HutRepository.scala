package io.github.spf3000.hutsapi

import java.util.UUID
import scala.collection.mutable.ListBuffer
import cats.implicits._
import io.github.spf3000.hutsapi.entities._
import cats.effect.Sync
import cats.effect.IO

trait InMemoryStore[L[_], F[_], A] {
  def add(a: A): F[Unit]
  def remove(a: A): F[Unit]
  def find(f: A => Boolean): F[Option[A]]
}

object InMemoryStore {
  case class LbImpl[F[_], A](lb: ListBuffer[A])(implicit F: Sync[F]) {
    val impl = new InMemoryStore[ListBuffer, F, A] {
      def add(a: A): F[Unit] = F.delay(lb += a)
      def remove(a: A): F[Unit] = F.delay(lb -= a)
      def find(f: A => Boolean): F[Option[A]] = F.delay(lb.find(f))
    }
  }
  object LbImpl {
    implicit def apply[F[_]: Sync, A]: LbImpl[F, A] =
      LbImpl(new ListBuffer[A]())
  }
  implicit val live: InMemoryStore[ListBuffer, IO, (String, Hut)] =
    LbImpl.apply[IO, (String, Hut)].impl
}

trait Random[F[_]] {
  def randomUUID: F[String]
}

object Random {
  implicit def syncRandom[F[_]](implicit F: Sync[F]) = new Random[F] {
    def randomUUID = F.delay { UUID.randomUUID().toString }
  }
}

trait Repository[L[_], F[_], A] {
  type IdA = (String, A)
  def get(id: String): F[Option[IdA]]
  def insert(a: A): F[String]
  def update(a: IdA): F[Unit]
  def delete(f: IdA => Boolean): F[Unit]
}
object Repository {
  case class Impl[L[_], F[_], A](S: InMemoryStore[L, F, (String, A)])
    (implicit F: Sync[F], R: Random[F]) {
    val impl = new Repository[L, F, A] {
      def get(id: String): F[Option[IdA]] = S.find(_._1 == id)
      def insert(a: A): F[String] =
        for {
          uuid <- R.randomUUID
          _ <- S.add((uuid, a))
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
  object Impl {
    def empty[L[_], F[_], A](implicit F: Sync[F],
                             S: InMemoryStore[L, F, (String, A)]) =
      Repository.Impl[L, F, A](S).impl
  }
}

package io.github.spf3000.hutsapi

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import entities.Hut
import io.circe.generic.auto._

object Main extends IOApp {
  def run(args: List[String]) =
    HutServer
      .stream[IO, Hut]("huts", InMemoryStore.LBStore.empty[IO,(String,Hut)].unsafeRunSync())
      .compile
      .drain
      .as(ExitCode.Success)
}


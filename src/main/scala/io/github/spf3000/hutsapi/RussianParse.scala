package io.github.spf3000.hutsapi

import matryoshka._
import matryoshka.implicits._
import scala.xml._
import matryoshka.data._

import scalaz._
import Scalaz._
import spire.math.Natural
import spire.math.Natural._


sealed trait X[A]

object X {

case class El[A](label: String, childs: IList[A]) extends X[A]

case class OTag(name: String, start: Natural, length: Natural)


implicit val XTraverse = new Traverse[X] {
  def traverseImpl [G[_]: Applicative, A, B](fa: X[A])(f: A => G[B]) =
  fa match {
    case El(l,a) => a.traverse(f) map (x => El(l, x))
}
}

val stateAlg: AlgebraM[State[Natural, ?], X, IList[OTag]] = {
  case El(l,x) =>
     for {
    _ <- State.modify[Natural](_+getStart(l))
    s <- State.get[Natural]
     } yield (OTag(l, s, Natural(l.length)) :: x.flatten)
  }


private def getStart(n: String): Natural = Natural(n.length + 1)
private def getEnd(n: String): Natural = Natural(n.length + 2)

}


/**
 *@spf3000 IIUC, I think you could use AlgebraM[State[Natural, ?], X, Seq[OTag]] (i.e., X[Seq[OTag]] => State[Natural, Seq[OTag]]). So then at each node you take in the current offset, add the size of the open tag, pass that down through the sequence of children, then add the length of the end tag to get the output state.
_.cataM(stateAlg).evalState(0)

 I guess once I work that out I will see what it produces. I think it's not going to work as I need to provide a slightly different function for combining the siblings as opposed to descendents, i.e. for each of th
 *
**/



package com.elsevier.entellect.xml

import scala.xml._


object Highlighter {

  case class OTag(name: String, start: Int, end: Int, attributes: String)
  case class HalfTag(pos: Int, t: String)

  def splitTag(ot: OTag, className: String): Seq[HalfTag] = {
    val ls = s"<$className>"
    val rs = s"</$className>"
    Seq(HalfTag(ot.start , ls), HalfTag(ot.end, rs))
  }


  def insertTag(tag: HalfTag, xs: String): String = {
      val (l,r) = xs.splitAt(tag.pos)
      println(l)
      println(r)
      l ++ tag.t ++ r
  }

    def highlight(xs: String, annotations: OTag, className: String) =
      xs.take(annotations.start) ++
     xs.slice(annotations.start, annotations.end) ++  s"</$className>" ++ xs.drop(annotations.end)



  def highlightAll(xs: String, annotations: Seq[OTag], className: String): String = {
    val halfTags = annotations.flatMap(ot => splitTag(ot, className))
    val sTags = halfTags.sortBy(_.pos).reverse
    sTags.foldLeft(xs)((s,ht) => insertTag(ht,s))

  }


}

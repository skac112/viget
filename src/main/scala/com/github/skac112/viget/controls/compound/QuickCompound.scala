package com.github.skac112.viget.controls.compound

import shapeless.{HList, the}

//import Compound._
//
//case class QuickCompound[C <: HList, T](override val controls: C, htmlId: String)
//                                       (implicit outExtr: OutExtractor.Aux[C, T],
//                                        connector: Connector.Aux[T, C],
//                                        markupMaker: MarkupMaker[C]) extends Compound[C, T]

import Compound2._

case class QuickCompound[QC <: HList, T](override val controls: QC, htmlIdO: Option[String] = None)
                                        (implicit aOutExtr: OutExtractor.Aux[QC, T],
                                         aConnector: Connector.Aux[T, QC],
                                         aMarkupMaker: MarkupMaker[QC])
  extends Compound2[T] {
  override val htmlId = htmlIdO.getOrElse(super.htmlId)
  import Compound2._
  type C = QC
  override implicit val outExtr: OutExtractor.Aux[QC, T] = aOutExtr
  override implicit val connector: Connector.Aux[T, QC] = aConnector
  override implicit val markupMaker: MarkupMaker[QC] = aMarkupMaker
}

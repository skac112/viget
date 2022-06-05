package com.github.skac112.viget.controls.compound

import shapeless.{HList, the}
import Compound._

/**
  *
  * @param controls
  * @param htmlIdO
  * @param aOutExtr
  * @param aConnector
  * @param aMarkupMaker
  * @tparam QC type of created controls (e. g. some HList subtype)
  * @tparam T type of controlled data
  */
case class QuickCompound[QC <: HList, T](override val controls: QC, htmlIdO: Option[String] = None)
                                        (implicit aOutExtr: OutExtractor.Aux[QC, T],
                                         aConnector: Connector.Aux[T, QC],
                                         aMarkupMaker: MarkupMaker[QC])
  extends Compound[T] {
  override val htmlId = htmlIdO.getOrElse(super.htmlId)
  import Compound._
  type C = QC
  override implicit val outExtr: OutExtractor.Aux[QC, T] = aOutExtr
  override implicit val connector: Connector.Aux[T, QC] = aConnector
  override implicit val markupMaker: MarkupMaker[QC] = aMarkupMaker
}

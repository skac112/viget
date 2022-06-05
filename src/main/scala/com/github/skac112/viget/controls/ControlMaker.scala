package com.github.skac112.viget.controls

import com.github.skac112.viget.controls.compound.Compound.Connector.Aux
import com.github.skac112.viget.controls.compound.Compound.MarkupMaker
import com.github.skac112.viget.controls.compound.QuickCompound
import rxscalajs.Observable
import shapeless.{HList, HNil}
import shapeless._

trait CntListMaker[H <: HList] {
  type C <: HList
  def make: C
}

object CntListMaker {
  type Aux[H <: HList, CC <: HList] = CntListMaker[H] { type C = CC }
  def apply[H <: HList](implicit inst: CntListMaker[H]): Aux[H, inst.C] = inst

  implicit val hNilMaker: Aux[HNil, HNil] = new CntListMaker[HNil] {
    type C = HNil
    override def make = HNil
  }

  /**
    * Recurrence-based maker for HList viget control.
    * @param cntMaker
    * @param cntListMaker
    * @tparam T type of first element of HList to be controlled
    * @tparam H type of rest of HList to be controlled
    * @tparam O type of HList of controls created to control H
    * @return
    */
  implicit def hListMaker[T, H <: HList, O <: HList]
    (implicit cntMaker: ControlMaker[T], cntListMaker: CntListMaker.Aux[H, O]): Aux[T :: H, VigetControl[T] :: O] =
    new CntListMaker[T :: H]  {
      type C = VigetControl[T] :: O
      override def  make = cntMaker.make :: cntListMaker.make
    }
}

trait ControlMaker[T] {
  def make: VigetControl[T]
}

import com.github.skac112.viget.controls.compound.Compound._

object ControlMaker {
  def apply[T](implicit inst: ControlMaker[T]): ControlMaker[T] = inst
  implicit val dblCnt: ControlMaker[Double] = new ControlMaker[Double] { override def make = RangeIndFloat(0.0, 10.0) }
  implicit val colorCnt: ControlMaker[Color] = new ControlMaker[Color] { override def make = ColorSelect() }

  /**
    *
    * @param cntListMaker
    * @param outExtr
    * @param connector
    * @param markupMaker
    * @tparam H type of HList to be controlled
    * @tparam C type of controls wrapped in QuickCompound (inferred)
    * @return
    */
  implicit def hListCnt[H <: HList, C <: HList](implicit cntListMaker: CntListMaker.Aux[H, C],
                                                outExtr: OutExtractor.Aux[C, H],
                                                connector: Connector.Aux[H, C],
                                                markupMaker: MarkupMaker[C]) : ControlMaker[H] =
    new ControlMaker[H] {
      override def make: VigetControl[H] = QuickCompound[C, H](cntListMaker.make)
    }

  /**
    *
    * @param generic
    * @param cntListMaker
    * @param outExtr
    * @param connector
    * @param markupMaker
    * @tparam C case class type to be controlled
    * @tparam H HList subtype representing C (inferred)
    * @tparam L type of controls wrapped in QuickCompound (inferred)
    * @return
    */
  implicit def caseCnt[C, H <: HList, L <: HList]
    (implicit generic: Generic.Aux[C, H],
     cntListMaker: CntListMaker.Aux[H, L],
     outExtr: OutExtractor.Aux[L, C],
     connector: Connector.Aux[C, L],
     markupMaker: MarkupMaker[L]) = new ControlMaker[C] {
    override def make: VigetControl[C] = QuickCompound[L, C](cntListMaker.make)
  }
}


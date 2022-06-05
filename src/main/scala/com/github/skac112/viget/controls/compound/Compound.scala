package com.github.skac112.viget.controls.compound

import com.github.skac112.viget.controls.VigetControl
import org.scalajs.dom.raw.HTMLInputElement
import org.scalajs.dom.{Event, document}
import rxscalajs.Observable
import scalatags.JsDom.Frag
import scalatags.JsDom.all._
import shapeless._

object Compound {
  type Aux[T, CC] = Compound[T] { type C = CC }

  /**
    * Extracts out observable from hlist of controls
    */
  trait OutExtractor[L <: HList] {
    type Out
    def extract(controls: L): Observable[Out]
  }

  object OutExtractor {
    type Aux[L <: HList, O] = OutExtractor[L] { type Out = O }
    def apply[L <: HList](implicit inst: OutExtractor[L]): Aux[L, inst.Out] = inst

    implicit def hNilValueExtr: Aux[HNil, HNil] = new OutExtractor[HNil] {
      type Out = HNil
      override def extract(controls: HNil): Observable[HNil] = Observable.just[HNil](HNil)
    }

    implicit def hListExtr[VC <: VigetControl[T], T, H <: HList, O <: HList](implicit hExtr: OutExtractor.Aux[H, O]):
      Aux[VC :: H, T :: O] = new OutExtractor[VC :: H] {
      type Out = T :: O

      override def extract(controls: VC :: H): Observable[Out] =
        controls.head.out.combineLatestWith(hExtr.extract(controls.tail))((el: T, list: O) => el :: list)
    }

    implicit def caseExtr[L <: HList, C, O <: HList](implicit generic: Generic.Aux[C, O], hListExtr: OutExtractor.Aux[L, O]): Aux[L, C] =
      new OutExtractor[L] {
        type Out = C
        override def extract(controls: L): Observable[Out] = hListExtr.extract(controls) map { generic.from }
      }
  }

  /**
    * Connects observable of type U to hlist of controls
    * @tparam U
    */
  trait Connector[U] {
    type Out <: HList
    def connect(in: Observable[U], controls: Out): Unit
  }

  object Connector {
    type Aux[U, C <: HList] = Connector[U] { type Out = C }
    def apply[U](implicit inst: Connector[U]): Aux[U, inst.Out] = inst

    implicit def hNilConnector: Aux[HNil, HNil] = new Connector[HNil] {
      type Out = HNil
      override def connect(in: Observable[HNil], controls: HNil): Unit = {}
    }

    implicit def hListConnector[T, H <: HList, VT <: VigetControl[T], O <: HList](implicit hConnector: Connector.Aux[H, O]):
    Aux[T :: H, VT :: O] = new Connector[T :: H] {
      type Out = VT :: O

      override def connect(in: Observable[T :: H], controls: VT :: O): Unit = {
        controls.head.connect(in map {_.head})
        hConnector.connect(in map { _.tail }, controls.tail);
      }
    }

    implicit def caseConnector[C, L <: HList, O <: HList](implicit generic: Generic.Aux[C, L], hListConnector: Connector.Aux[L, O]): Aux[C, O] =
      new Connector[C] {
        type Out = O
        override def connect(in: Observable[C], controls: O): Unit = hListConnector.connect(in map { generic.to }, controls)
      }
  }

  trait MarkupMaker[C <: HList] {
    def markup(cnts: C): Seq[Frag]
  }

  object MarkupMaker {
    implicit val HNilMarkupMaker: MarkupMaker[HNil] = (cnts: HNil) => Seq()

    implicit def hListMarkupMaker[H <: VigetControl[_], C <: HList](implicit cm: MarkupMaker[C]): MarkupMaker[H :: C] =
      (cnts: H :: C) => cnts.head.markup +: cm.markup(cnts.tail)
  }
}

import com.github.skac112.viget.controls.compound.Compound._

/**
  * @tparam T type of controlled data
  */
abstract class Compound[T]() extends VigetControl[T] {

  /**
    * Type of created controls.
    */
  type C <: HList

  def controls: C
  implicit val outExtr: OutExtractor.Aux[C, T]
  implicit val connector: Connector.Aux[T, C]
  implicit val markupMaker: MarkupMaker[C]
  private def controlsMarkup: Seq[Frag] = markupMaker.markup(controls)

  override lazy val markup: Frag = div(id := htmlId)(
    updateBtn,
    batchUpdateCb,
    controlsMarkup,
    onchange := changeHndlr
    )

  override def mode: VigetControl.Mode.Mode = ???

  private lazy val changeHndlr = {(event: Event) =>
    println("change!")
  }

  /**
    * "Hierarchical" id.
    */
  private def hierId(parentId: String, childIdPart: String) = s"${parentId}_${childIdPart}"

  /**
    * Hierarchical id of first-level ancestors.
    * @param childIdPart
    */
  private def firstLevelId(childIdPart: String) = hierId(htmlId, childIdPart)

  lazy val updateBtn = button(name := "update_btn", id := firstLevelId("update_btn"))("Update")
  lazy val batchUpdateCb = input(`type` := "checkbox", name := "batch_update_cb", id := batchUpdateCbId)
  lazy val updateBtnId = firstLevelId("update_btn")
  lazy val batchUpdateCbId = firstLevelId("batch_update_cb")

  override def connect(in: Observable[T]): Unit = {
    connector.connect(in, controls)
//    val change_event = document.createEvent("MutationEvents")
//    change_event.initEvent("change", false, true)
//    markupDom.dispatchEvent(change_event)
  }

  /**
    * Option of observable of updates of controlled value.
    */
  lazy val updateObs: Observable[T] = outExtr.extract(controls)

  /**
    * Option of observable of updates of controlled value filtered by batch update button state.
    */
  lazy val filtUpdateObs: Observable[T] = updateObs.withLatestFrom(isBatchUpdateObs) filter
    { kv: (T, Boolean) => !kv._2 } map { kv: (T, Boolean) => kv._1}

  /**
    * Observable with batch updates. Emitting is initiated by using update button but it contains T data.
    */
  lazy val batchUpdateObs = updateBtnObs.withLatestFrom(updateObs) map { _._2 }

  /**
    * Observable with update button event. Emitting is initiated by using update button.
    */
  lazy val updateBtnObs = Observable.fromEvent(updateBtnDom, "click")

  lazy val isBatchUpdateObs: Observable[Boolean] = Observable.fromEvent(batchUpdateCbDom, "change") map
    {(ev: Event) => ev.srcElement.asInstanceOf[HTMLInputElement].checked} merge Observable.just(false)

  private def updateBtnDom = document.getElementById(updateBtnId).asInstanceOf[HTMLInputElement]

  private def batchUpdateCbDom = document.getElementById(batchUpdateCbId).asInstanceOf[HTMLInputElement]

  override lazy val out = filtUpdateObs merge batchUpdateObs
}

package com.github.skac112.viget.controls
import org.scalajs.dom.{Event, document}
import org.scalajs.dom.raw.HTMLInputElement
import scalatags.JsDom.all._
import rxscalajs._
import VigetControl._

/**
  * Control for numeric values with slider and number input field.
  * @param id
  * @param minVal
  * @param maxVal
  * @param inO
  */
case class RangeIndFloat(minVal: Double,
                         maxVal: Double,
                         htmlIdO: Option[String] = None)
  extends VigetControl[Double] {
  override val htmlId = htmlIdO.getOrElse(super.htmlId)
//  lazy val markupDom = markup.render
  override lazy val markup: Frag = div(id := htmlId)(rangeInput, numberInput)
  override lazy val mode = Mode.READ_WRITE
  override lazy val out = out1 merge out2
  private lazy val stepVal = .01 * (maxVal - minVal)
  private lazy val rangeId = s"${htmlId}_range"
  private lazy val numInputId = s"${htmlId}_numinput"
  private def rangeInputDom = document.getElementById(rangeId).asInstanceOf[HTMLInputElement]
  private def numInputDom = document.getElementById(numInputId).asInstanceOf[HTMLInputElement]

  private lazy val rangeChangeHndlr = {(event: Event) =>
    numInputDom.value = event.target.asInstanceOf[HTMLInputElement].value
  }

  private lazy val numberChangeHndlr = {(event: Event) =>
    rangeInputDom.value = event.target.asInstanceOf[HTMLInputElement].value
  }

  private lazy val rangeInput = input(`type` := "range", id := rangeId, min := minVal, max := maxVal, step := stepVal, verticalAlign := "middle", onchange := rangeChangeHndlr)()
  private lazy val numberInput = input(`type` := "number", id := numInputId, min := minVal, max := maxVal, step := stepVal, onchange := numberChangeHndlr)()
  private lazy val out1 = Observable.fromEvent(rangeInputDom, "change") map {_.target.asInstanceOf[HTMLInputElement].value.toDouble}
  private lazy val out2 = Observable.fromEvent(numInputDom, "change") map {_.target.asInstanceOf[HTMLInputElement].value.toDouble}
//  private lazy val rangeInputDom = markupDom.firstChild.asInstanceOf[TMLInputElement]
//  private lazy val numberInputDom = markupDom.lastChild.asInstanceOf[HTMLInputElement]


  override def connect(in: Observable[Double]) = in.subscribe(_ match {
    case double_val: Double => {
      val str_val = double_val.toString
      rangeInputDom.value = str_val
      numInputDom.value = str_val
      val change_event = document.createEvent("MutationEvents")
      change_event.initEvent("change", false, true)
      rangeInputDom.dispatchEvent(change_event)
    }})
}

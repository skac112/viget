package com.github.skac112.viget.controls

import VigetControl._
import org.scalajs.dom.{Event, document}
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLInputElement
import scalatags.JsDom.all._
import rxscalajs.Observable
import scalatags.JsDom.all.{Frag, `type`, div, input, max, min, step}

import scala.scalajs.js

case class ColorSelect(htmlIdO: Option[String] = None) extends VigetControl[Color] {
  override val htmlId = htmlIdO.getOrElse(super.htmlId)
  lazy val colorChangeHndlr = {(event: Event) => {textInputDom.value = event.target.asInstanceOf[HTMLInputElement].value}}
  lazy val textChangeHndlr = {(event: Event) => {colorInputDom.value = event.target.asInstanceOf[HTMLInputElement].value}}
  lazy val colorInput = input(`type` := "color", min := 10, id := colorInputId, onchange := colorChangeHndlr)()
  lazy val textInput = input(`type` := "text", id := textInputId, onchange := textChangeHndlr)()
  override lazy val markup: Frag = div(id := htmlId)(colorInput, textInput)
  override lazy val mode = Mode.READ_WRITE
  private lazy val colorInputId = s"${htmlId}_color"
  private lazy val textInputId = s"${htmlId}_text"

  lazy val outColorInput = Observable.fromEvent(colorInputDom, "change") map {ev: Event =>
    Color(ev.target.asInstanceOf[HTMLInputElement].value)}

  lazy val outTextInput = Observable.fromEvent(textInputDom, "change") map {ev: Event =>
    Color(ev.target.asInstanceOf[HTMLInputElement].value)}

  override lazy val out = outColorInput merge outTextInput

//  lazy val markupDom = markup.render
//  private lazy val colorInputDom = markupDom.firstChild.asInstanceOf[HTMLInputElement]
//  private lazy val textInputDom = markupDom.lastChild.asInstanceOf[HTMLInputElement]

  private def colorInputDom = document.getElementById(colorInputId).asInstanceOf[HTMLInputElement]
  private def textInputDom = document.getElementById(textInputId).asInstanceOf[HTMLInputElement]

  override def connect(in: Observable[Color]) = in.subscribe(value => {
    val str_val = value.hexValue
    colorInputDom.value = str_val
    textInputDom.value = str_val
    val change_event = document.createEvent("MutationEvents")
    change_event.initEvent("change", false, true)
    colorInputDom.dispatchEvent(change_event)
  })
}


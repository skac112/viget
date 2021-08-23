package com.github.skac112.viget.controls

import scalatags.JsDom._
import rxscalajs._

object VigetControl {
  object Mode extends Enumeration {
    type Mode = Value
    val READ, WRITE, READ_WRITE = Value
  }

  val r = new scala.util.Random(31)
}

trait VigetControl[T] {
  def htmlId: String = VigetControl.r.nextString(10)
  def markup: Frag
  def mode: VigetControl.Mode.Mode
  def out: Observable[T]
  def connect(in: Observable[T]): Unit = ()
  lazy val markupDom = markup.render
}

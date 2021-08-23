package com.github.skac112.viget

import org.scalajs.dom._
import rxscalajs._
import scalatags.JsDom.all._

object Datatype extends Enumeration {
  type Datatype = Value
  val DT_FLOAT, DT_INT, DT_COLOR, DT_WIDTH = Value
}

object ControlType extends Enumeration {
  type ControlType = Value
  val CT_DEFAULT, CT_RANGE_IND, CT_NUM = Value
}

import Datatype._
import ControlType._

object Viget {
  type CntrReadFun[T] = T => Any
  type CntrUpdateFun[T] = (T, Any) => T
  type SvgReadFun[T] = Unit => T
  type SvgUpdateFun[T] = (T, T) => Unit

  case class Control[T](label: String, datatype: Datatype, controlType: ControlType, readFun: CntrReadFun[T], updateFun: CntrUpdateFun[T]) {
    def actualType = controlType match {
      case CT_DEFAULT => datatype match {
        case DT_FLOAT =>
      }
      case _ => controlType
    }
  }

  case class Options(updateDelay: Int = 2000, explicitUpdate: Boolean = false)
}

import Viget._

trait VigetSpec[T] {
  def controls: Seq[Control[T]]
  def readFun: SvgReadFun[T]
  def updateFun: SvgUpdateFun[T]
  def options: Options = Options()
}

case class DefaultVigetSpec[T](override val controls: Seq[Control[T]],
                               override val readFun: SvgReadFun[T],
                               override val updateFun: SvgUpdateFun[T],
                               override val options: Options = Options())
extends VigetSpec[T]

case class VigetExec[T](elementId: String, spec: VigetSpec[T], initControlObj: T, objSetO: Option[Observable[T]] = None) {
  val controlObs: Observable[T] = build

  private def build(): Observable[T] = {
    val container = document.getElementById(elementId)
    container.appendChild(cntPanelFrag.render)
    container.appendChild(svgCanvasFrag.render)
    val obs1 = Observable.fromEvent(document.querySelector(s"#${elementId} .viget-control"), "change").map(eventToControlObj _)
    val obs = initControlObj +: obs1
    obs.subscribe({obj: T => draw(obj)})
    obs
  }

  private def cntPanelFrag: Frag = div(width := 200)(for (cnt <- spec.controls) yield cntFrag(cnt))

  private def svgCanvasFrag: Frag = div(minHeight := 300, backgroundColor := "yellow")

//  private def eventToControlObj(event: Event): T = ???
  private def eventToControlObj(event: Event): T = initControlObj

  private def cntFrag(control: Control[T]): Frag = ???
//    val cnt_int = control.
//  }

  private def draw(controlObj: T): Unit = {

  }

  def setControlObj(newControlObj: T): Unit = {

  }

  def buildVigetField: Frag = {
    div(label("label"), span("value"), input())
  }

//  def fromDomEvent = {
//    Observable.fromEvent(document.getElementById("btn"),"click")
//      .mapTo(1)
//      .scan(0)(_ + _)
//      .subscribe(n => println(s"Clicked $n times"))
//  }
}



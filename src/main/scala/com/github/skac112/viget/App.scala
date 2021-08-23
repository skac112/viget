package com.github.skac112.viget

import com.github.skac112.viget.controls.compound.{Compound, Compound2, QuickCompound}
import controls._
import org.scalajs.dom

import scala.scalajs.js
import org.scalajs.dom._
import scalatags.JsDom.all._
import rxscalajs._
import shapeless._

import scala.scalajs.js.annotation.JSGlobal

//@js.native
//@JSGlobal
//object Window extends dom.Window {
//  val customElements: CustomElementsRegistry = js.native
//}
//
//@js.native
//trait CustomElementsRegistry extends js.Any {
//  def define(name: String, definition: Any ) : Unit = js.native
//}
//
//@js.native
//@JSGlobal
//class HTMLTemplateElement extends HTMLElement {
//  val content: HTMLElement = js.native
//}
//
//@js.native
//@JSGlobal
//class HTMLElement extends org.scalajs.dom.raw.HTMLElement {
//  def attachShadow(options: js.Any) : org.scalajs.dom.raw.HTMLElement = js.native
//}

import ControlMaker._

object App {
  case class Sample(x: Double, y: Double)
  case class NestedSample(sample: Sample, color: Color)

  def main(args: Array[String]): Unit = {
    println("Hello world from viget 31!")
//    val c1 = RangeIndFloat(0, 10)
//    val c2 = RangeIndFloat(0, 10)
//    val c3 = ColorSelect()
//
//    val sampleCompound = QuickCompound[RangeIndFloat :: RangeIndFloat :: HNil, Sample](
//      c1 :: c2 :: HNil)
//    val cnts = sampleCompound :: c3 :: HNil
////    type CntsType = Compound[RangeIndFloat :: RangeIndFloat :: HNil, Sample]  :: ColorSelect :: HNil
//
//    type CntsType = Compound2[Sample]  :: ColorSelect :: HNil
//    val comp1 = QuickCompound[CntsType, NestedSample](cnts)
    import ControlMaker._
    val cm = the[ControlMaker[NestedSample]]
    val comp1 = cm.make
    document.body.appendChild(comp1.markupDom)
    comp1.out.subscribe(_ => println("update"))
    comp1.asInstanceOf[QuickCompound[_, _]].batchUpdateObs.subscribe(println(_))
    comp1.asInstanceOf[QuickCompound[_, _]].filtUpdateObs.subscribe(println(_))
    comp1.asInstanceOf[QuickCompound[_, _]].updateBtnObs.subscribe(println(_))
    comp1.asInstanceOf[QuickCompound[_, _]].isBatchUpdateObs.subscribe(println(_))
    // providing sample data
    comp1.connect(Observable.just(NestedSample(Sample(1.0, 3.0), Color("#ffff00"))))

//    import ControlMaker._
//    val cm = the[ControlMaker[Double]]
//    val cd1 = cm.make
//    document.body.appendChild(cd1.markupDom)
//    cd1.connect(Observable.just(5.0))

//    def makeDoubleCnt(implicit cm: ControlMaker[Double]): VigetControl[Double] = cm.make
//    val cd2 = makeDoubleCnt
//    document.body.appendChild(cd2.markupDom)
//    cd2.connect(Observable.just(5.0))
  }
}

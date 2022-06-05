package com.github.skac112.viget

import com.github.skac112.viget.controls.compound.{Compound, QuickCompound}
import controls._
import org.scalajs.dom
import scala.scalajs.js
import org.scalajs.dom._
import scalatags.JsDom.all._
import rxscalajs._
import shapeless._
import scala.scalajs.js.annotation.JSGlobal
import ControlMaker._

object App {
  case class Sample(x: Double, y: Double)
  case class NestedSample(sample: Sample, color: Color)

  def main(args: Array[String]): Unit = {
    println("Hello world from viget 32!")
    import ControlMaker._
    // Creating control maker for appropriate type of data (NestedSample)
    val cm = the[ControlMaker[NestedSample]]
    // Creating control for appropriate type of data (NestedSample)
    val comp1 = cm.make
    // Rendering markup of control and adding it do document
    document.body.appendChild(comp1.markupDom)
    comp1.out.subscribe(_ => println("update"))
    comp1.asInstanceOf[QuickCompound[_, _]].batchUpdateObs.subscribe(println(_))
    comp1.asInstanceOf[QuickCompound[_, _]].filtUpdateObs.subscribe(println(_))
    comp1.asInstanceOf[QuickCompound[_, _]].updateBtnObs.subscribe(println(_))
    comp1.asInstanceOf[QuickCompound[_, _]].isBatchUpdateObs.subscribe(println(_))
    // providing sample data
    comp1.connect(Observable.just(NestedSample(Sample(1.0, 3.0), Color("#ffff00"))))
  }
}

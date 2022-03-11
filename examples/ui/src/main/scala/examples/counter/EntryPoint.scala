package examples.counter

import org.scalajs.dom.html.{Div, Element}
import uiglue.{EventLoop, UIState}

import scala.concurrent.Future

object EntryPoint {

  def main(args: Array[String]): Unit = {
    val document = org.scalajs.dom.window.document
    val newDiv = document.createElement("div").asInstanceOf[Div]
    document.body.replaceChildren(newDiv)
    init(newDiv)
  }

  def init(div: Div): Unit = {
    val state = CounterState()

    val f: (UIState[CounterEvent], CounterEvent => Unit) => Unit =
      (state, f) =>
        Displayer.rootComponent(state.asInstanceOf[CounterState], f).renderIntoDOM(div)

    val loop = EventLoop.createLoop(state, f, List(Increase, Increase, Increase))

    implicit val ec: scala.concurrent.ExecutionContext = org.scalajs.macrotaskexecutor.MacrotaskExecutor

    Future {
      zio.Runtime.default.unsafeRun(loop)
    }
  }
}
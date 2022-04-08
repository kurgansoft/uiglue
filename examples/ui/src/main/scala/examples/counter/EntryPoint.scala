package examples.counter

import org.scalajs.dom.html.{Div, Element}
import uiglue.EventLoop.EventHandler
import uiglue.{EventLoop, UIState}

import scala.concurrent.Future

object EntryPoint {

  implicit val ec: scala.concurrent.ExecutionContext = org.scalajs.macrotaskexecutor.MacrotaskExecutor

  def main(args: Array[String]): Unit = {
    val document = org.scalajs.dom.window.document
    val newDiv = document.createElement("div").asInstanceOf[Div]
    document.body.replaceChildren(newDiv)
    init(newDiv)
  }

  def init(div: Div): Unit = {
    val state = CounterState()

    val renderFunction: (UIState[CounterEvent], EventHandler[CounterEvent]) => Unit =
      (state, eventHandler) =>
        Displayer.rootComponent(state.asInstanceOf[CounterState], eventHandler).renderIntoDOM(div)

    val loop = EventLoop.createLoop(state, renderFunction, List(Increase, Increase, Increase))

    Future {
      zio.Runtime.default.unsafeRun(loop)
    }
  }
}
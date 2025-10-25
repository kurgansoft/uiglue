package examples.counter

import org.scalajs.dom.html.Div
import uiglue.EventLoop.EventHandler
import uiglue.{EventLoop, UIState}
import zio.Unsafe
import zio.internal.stacktracer.Tracer

import scala.concurrent.Future

object EntryPoint {

  implicit val tracer: Tracer = Tracer.instance

  implicit val unsafe: Unsafe = Unsafe.unsafe(x => x)

  implicit val ec: scala.concurrent.ExecutionContext = org.scalajs.macrotaskexecutor.MacrotaskExecutor

  def main(args: Array[String]): Unit = {
    val document = org.scalajs.dom.window.document
    val newDiv = document.createElement("div").asInstanceOf[Div]
    document.body.replaceChildren(newDiv)
    init(newDiv)
  }

  private def init(div: Div): Unit = {
    val state = CounterState()

    val renderFunction: (UIState[CounterEvent, Any], EventHandler[CounterEvent]) => Unit =
      (state, eventHandler) =>
        Displayer.rootComponent(state.asInstanceOf[CounterState], eventHandler).renderIntoDOM(div)

    val loop = EventLoop.createLoop(state, renderFunction, List(Increase, Increase, Increase))

    Future {
      zio.Runtime.default.unsafe.run(loop)
    }
  }
}
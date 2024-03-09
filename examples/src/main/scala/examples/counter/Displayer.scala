package examples.counter

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{Callback, ScalaComponent}
import uiglue.EventLoop

object Displayer {

  val rootComponent =
    ScalaComponent.builder[(CounterState, EventLoop.EventHandler[CounterEvent])]("RootComponent")
      .render_P({
        case (state, eventEmitter) =>
          div(
            button("-", onClick --> Callback {
              eventEmitter(Decrease)
            }, disabled:= !state.decreasePossible),
            div(state.number),
            button("+", onClick --> Callback {
              eventEmitter(Increase)
            })
          )
      }).build
}

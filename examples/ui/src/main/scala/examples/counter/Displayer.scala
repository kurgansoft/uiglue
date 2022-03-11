package examples.counter

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{Callback, ScalaComponent}

object Displayer {

  val rootComponent =
    ScalaComponent.builder[(CounterState, CounterEvent => Unit)]("RootComponent")
      .render_P(pair => {
        val (state, eventEmitter) = pair
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

package examples.counter

import uiglue.{EventLoop, UIState}
import zio.UIO

case class CounterState(number: Int = 0) extends UIState[CounterEvent] {

  override def processEvent(event: CounterEvent): (UIState[CounterEvent], EventLoop.EventHandler[CounterEvent] => UIO[List[CounterEvent]]) = event match {
    case Increase =>
      val newState = this.copy(number = number + 1)
      if (newState.number == 10)
        (newState, UIO[List[CounterEvent]] {
          println("We just reached no. 10! Hurray!")
          List.empty
        })
      else
        newState
    case Decrease => if (number > 0) this.copy(number = number -  1) else this
  }

  val decreasePossible: Boolean = number > 0
}

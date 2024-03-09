package examples.counter

import uiglue.Event

abstract sealed class CounterEvent extends Event

case object Increase extends CounterEvent
case object Decrease extends CounterEvent

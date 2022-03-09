package uiglue

import zio.UIO

import scala.language.implicitConversions

trait UIState[E <: Event] {

  implicit def convert(state: UIState[E]): (UIState[E], UIO[List[E]]) = {
    (state, UIO.succeed(List.empty))
  }

  def processEvent(event: E): (UIState[E], UIO[List[E]])
}

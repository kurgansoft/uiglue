package uiglue

import zio.{UIO, ZIO}

import scala.language.implicitConversions

trait UIState[E <: Event] {

  implicit def convert(state: UIState[E]): (UIState[E], EventLoop.EventHandler[E] => UIO[List[E]]) =
    (state, _ => ZIO.succeed(List.empty))

  implicit def convert2(e: UIO[List[E]]): EventLoop.EventHandler[E] => UIO[List[E]] = _ => e

  def processEvent(event: E): (UIState[E], EventLoop.EventHandler[E] => UIO[List[E]])
}

package uiglue

import zio.{UIO, ZIO}

import scala.language.implicitConversions

trait UIState[E <: Event, Dependencies] {

  implicit def convert[D](state: UIState[E, Dependencies]): (UIState[E, Dependencies], EventLoop.EventHandler[E] => ZIO[Dependencies, Nothing, List[E]]) =
    (state, _ => ZIO.succeed(List.empty))

  implicit def convert2[D](e: UIO[List[E]]): EventLoop.EventHandler[E] => ZIO[Dependencies, Nothing, List[E]] = _ => e

  def processEvent(event: E): (UIState[E, Dependencies], EventLoop.EventHandler[E] => ZIO[Dependencies, Nothing, List[E]])
}

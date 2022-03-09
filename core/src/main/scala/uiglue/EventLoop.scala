package uiglue

import zio.{Queue, Ref, Task, UIO}

import scala.concurrent.Future

object EventLoop {

  def eventHandler[E <: Event](queue: Queue[E]): E => Unit = event => {
    Future {
      zio.Runtime.default.unsafeRun(queue.offer(event).map(_ => ()))
    }(org.scalajs.macrotaskexecutor.MacrotaskExecutor)
  }

  def createLoop[E <: Event](
                            initalState: UIState[E],
                            renderFunction: (UIState[E], E => Unit) => Unit,
                            bootStrapEvents: List[E] = List.empty
                            ): UIO[Unit] = {
    for {
      state <- Ref.make(initalState)
      queue <- Queue.unbounded[E]
      _ = renderFunction(initalState, eventHandler(queue))
      _ <- queue.offerAll(bootStrapEvents)
      _ <- queue.take.flatMap(event =>
        for {
          currentState <- state.get
          (newState, effect) = currentState.processEvent(event)
          _ <- state.set(newState)
          _ <- Task{renderFunction(newState, eventHandler(queue))}.orDie.fork
          _ <- effect.fork
        } yield ()
      ).forever
    } yield ()
  }
}

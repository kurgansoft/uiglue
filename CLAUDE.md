# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

uiglue is a small Redux-like state management library for Scala.js, built on ZIO. It is a two-module sbt/Scala 3 project: `core` (the library) and `examples` (a scalajs-react demo app that depends on `core`).

Prior art referenced in README.md: https://redux.js.org/ and https://diode.suzaku.io/

## Architecture

The core library implements a single-threaded, event-driven render loop:

- **`Event`** (`core/.../Event.scala`) — empty marker trait. Applications define their own sealed hierarchy of events extending it (see `CounterEvent` in examples).
- **`UIState[E <: Event, Dependencies]`** (`core/.../UIState.scala`) — the state contract. Application state case classes implement `processEvent(event: E)`, returning a pair of `(newState, effect)`, where `effect` is `EventHandler[E] => ZIO[Dependencies, Nothing, List[E]]` — i.e. handling an event can trigger a ZIO effect (with access to `Dependencies` in the environment) that produces follow-up events to feed back into the loop. Implicit conversions let `processEvent` match-branches return just a bare new state, or just a `UIO[List[E]]`, without hand-wrapping into the full tuple/function shape (see `CounterState.processEvent` for both shorthand forms in use).
- **`EventLoop`** (`core/.../EventLoop.scala`) — drives everything:
  1. Holds state in a ZIO `Ref` and events in an unbounded ZIO `Queue[E]`.
  2. `createEventHandler` wraps queue offers in a `scala.concurrent.Future` scheduled on `MacrotaskExecutor`, so UI callbacks (e.g. React `onClick`) can push events without blocking on the ZIO runtime directly.
  3. `createLoop(initialState, renderFunction, bootStrapEvents)` seeds the queue with `bootStrapEvents`, then loops forever: take an event → `processEvent` → update the `Ref` → fork the resulting effect (its produced events are re-offered to the queue) → synchronously call `renderFunction` with the new state. Rendering is intentionally synchronous/non-forked (see commit "rendering is no longer asynchronous").
  - Returns `ZIO[Dependencies, Nothing, Unit]`; the caller supplies `Dependencies` via the ZIO environment and runs it with `zio.Runtime.default.unsafe.run`.

The `examples/counter` module shows the intended usage pattern end-to-end:
- `CounterEvent` — event ADT (`Increase`/`Decrease`).
- `CounterState` — `UIState` implementation; demonstrates returning a bare new state, a state unchanged (`this`), and a full `(state, effect)` pair that runs a side effect (`println`) when a threshold is hit.
- `Displayer` — a scalajs-react `ScalaComponent` that renders `(CounterState, EventHandler[CounterEvent])` and dispatches events via the handler on click.
- `EntryPoint` — wires it together: builds initial state, defines `renderFunction` to mount the React component into a DOM node, calls `EventLoop.createLoop`, and runs the resulting ZIO effect with the default runtime inside a `Future`.

When adding a new UI built on uiglue, follow this same shape: define an `Event` ADT, a `UIState` case class/hierarchy implementing `processEvent`, a render function bridging state+handler to your UI framework, and an entry point that calls `EventLoop.createLoop` and runs it.

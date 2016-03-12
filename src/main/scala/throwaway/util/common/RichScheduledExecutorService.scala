// The MIT License (MIT)
//
// Copyright (c) 2015 Michael Pitidis
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
// CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
// SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package throwaway.util.common

import java.util.concurrent.{Callable, ScheduledExecutorService, ScheduledFuture}

import scala.concurrent.duration._

/** Convenience implicits for scheduling tasks in a more scala-friendly manner. */
object RichScheduledExecutorService {

  implicit class RichScheduledExecutorService(val underlying: ScheduledExecutorService) extends AnyVal {
    import Implicits.funToCallable
    def once[T](delay: FiniteDuration)(task: => T): ScheduledFuture[T] = {
      underlying.schedule(task, delay.length, delay.unit)
    }
    import Implicits.funToRunnable
    def fixedDelay(delay: FiniteDuration, initial: FiniteDuration = 0.seconds)(task: => Any): ScheduledFuture[_] = {
      underlying.scheduleWithFixedDelay(task, initial.toUnit(delay.unit).toLong, delay.length, delay.unit)
    }
    def fixedRate(interval: FiniteDuration, initial: FiniteDuration = 0.seconds)(task: => Any): ScheduledFuture[_] = {
      underlying.scheduleAtFixedRate(task, initial.toUnit(interval.unit).toLong, interval.length, interval.unit)
    }
  }

  object Implicits {
    import scala.language.implicitConversions

    implicit def funToRunnable(f: => Any): Runnable = {
      new Runnable { def run(): Unit = { f } }
    }

    implicit def funToCallable[T](f: => T): Callable[T] = {
      new Callable[T] { def call(): T = { f } }
    }
  }
}

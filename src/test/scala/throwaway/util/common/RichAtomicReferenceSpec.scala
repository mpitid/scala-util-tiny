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

import java.util.concurrent.atomic.{AtomicLong, AtomicReference}
import java.util.concurrent.{CountDownLatch, Executors}

import org.scalatest.{FlatSpec, MustMatchers}

import scala.concurrent.duration._

class RichAtomicReferenceSpec extends FlatSpec with MustMatchers {
  import MeldMap._
  import RichAtomicReference._
  import RichCountDownLatch._
  import RichScheduledExecutorService._

  "updating a reference" should "be thread-safe" in {
    val reps = 10
    val size = 1000
    val latch = new CountDownLatch(reps)
    val exec = Executors.newScheduledThreadPool(reps)
    val ref = new AtomicReference(Map[Int,Int]())
    val calls = new AtomicLong(0)
    val map = Map(1.to(size).map { case i => i -> i }: _*)
    // Submit concurrent tasks which merge the referenced map with another one
    // by summing keys that conflict. The test verifies we don't miss out on
    // any of the values in the reference.
    for (i <- 1.to(reps)) {
      exec.once(0.millis) {
        for (i <- 1.to(size)) {
          ref.update { m =>
            calls.incrementAndGet()
            m.meld(map) { _ + _ }
          }
        }
        latch.countDown()
      }
    }
    latch.await(10.seconds) must equal(true)
    ref.get() must equal(map.mapValues(_ * size * reps))
    // If calls > size * reps there was contention updating the reference.
    // While there is no guarantee that this holds, it's fairly unlikely that
    // it doesn't if the updates ran in parallel.
    calls.get() must be > (size * reps.toLong)
  }
}

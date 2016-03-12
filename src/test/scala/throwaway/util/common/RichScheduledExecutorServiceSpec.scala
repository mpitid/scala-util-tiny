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

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{CountDownLatch, Executors}

import org.scalatest.{FlatSpec, MustMatchers}

import scala.concurrent.duration._

class RichScheduledExecutorServiceSpec extends FlatSpec with MustMatchers {

  import RichCountDownLatch._
  import RichScheduledExecutorService._

  "fixed rate and fixed delay scheduling" should "schedule tasks repeatedly on an executor" in {
    val rate = 500.millis
    val reps = 5

    val exec1 = Executors.newScheduledThreadPool(reps * 2)
    val exec2 = Executors.newScheduledThreadPool(reps * 2)
    val latch = new CountDownLatch(reps * 2)
    val rateCount = new AtomicInteger(0)
    val delayCount = new AtomicInteger(0)
    val f1 = exec1.fixedRate(rate) {
      if (latch.getCount > 0) { // race here should not matter
        rateCount.incrementAndGet()
        latch.countDown()
      }
      sleep(rate * 2)
    }
    val f2 = exec2.fixedDelay(rate) {
      if (latch.getCount > 0) {
        delayCount.incrementAndGet()
        latch.countDown()
      }
      sleep(rate * 2)
    }
    latch.await(rate * reps * 5) must equal(true)
    f1.cancel(true)
    f2.cancel(true)
    // Ensure fixed rate gets more runs given a big enough pool
    rateCount.get() must be > reps
    delayCount.get() must be < rateCount.get()
  }

  "scheduling once" should "execute functions on an executor after a fixed delay" in {
    val exec = Executors.newSingleThreadScheduledExecutor()
    val latch = new CountDownLatch(1)
    val f = exec.once(1.second) {
      latch.countDown()
      42
    }
    latch.getCount must equal(1)
    latch.await(2.second) must equal(true)
    f.get() must equal(42)
  }

  def sleep(t: FiniteDuration): Unit = {
    Thread.sleep(t.toMillis)
  }
}

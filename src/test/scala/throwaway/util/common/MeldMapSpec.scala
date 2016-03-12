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

import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, MustMatchers}

class MeldMapSpec extends FlatSpec with MustMatchers with PropertyChecks {
  import MeldMap._

  "merging" should "preserve dominating elements" in {
    forAll { (m1: Map[Int, Int], m2: Map[Int, Int]) =>
      testOverlap(m1, m2, m1.meldWithKeys(m2) { case (_, _, v2) => v2 })
      testOverlap(m2, m1, m1.meldWithKeys(m2) { case (_, v1, _) => v1 })
      testOverlap(m1, m2, m1.meld(m2) { case (_, v2) => v2 })
      testOverlap(m2, m1, m1.meld(m2) { case (v1, _) => v1 })
    }
  }

  // Test assuming m prefers m2 on key clash
  def testOverlap[A, B](m1: Map[A, B], m2: Map[A, B], m: Map[A, B]) = {
    m.keySet must equal(m1.keySet.union(m2.keySet))
    m.values.toSet.intersect(m2.values.toSet) must equal(m2.values.toSet)
    m1.keySet.map {
      case k => k -> m2.getOrElse(k, m1(k))
    } must equal(m.filter { case (k, _) => m1.contains(k) }.toSet)
  }
}

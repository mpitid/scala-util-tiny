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

/** Convenience implicits for merging maps. */
object MeldMap {
  implicit class MeldMap[A, B](val m1: Map[A, B]) extends AnyVal {
    /** Merge two maps by applying a function on the values to resolve conflicts. */
    def meld(m2: Map[A, B])(combine: (B, B) => B): Map[A, B] = {
      meldWithKeys(m2) { (_, v1, v2) => combine(v1, v2) }
    }
    /** Merge two maps by applying a function on the key and values to resolve conflicts. */
    def meldWithKeys(m2: Map[A, B])(combine: (A, B, B) => B): Map[A, B] = {
      m2.foldLeft(m1) {
        case (m, (k, v2)) =>
          m + (k -> m.get(k).map(v1 => combine(k, v1, v2)).getOrElse(v2))
      }
    }
  }
}

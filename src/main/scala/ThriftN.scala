//   Copyright 2014 Commonwealth Bank of Australia
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package au.com.cba.omnia.audition

import au.com.cba.omnia.maestro.api._, Maestro._

import au.com.cba.omnia.audition.thrift._


trait ThriftN[T] {
  def partition: Partition[T, Int]
  def getId(x: T): Int
  def setId(x: T, i: Int): Unit
  def init: T
}

object ThriftN {
  
  def create[T : ThriftN](i: Int): T = {
    val t = implicitly[ThriftN[T]]
    val x: T = t.init
    t.setId(x, i)
    x
  }

  def id[T : ThriftN](x: T): Int =
    implicitly[ThriftN[T]].getId(x)

  implicit object ThriftN10 extends ThriftN[Thrift10] {
    def init: Thrift10 = new Thrift10
    def partition = Partition.byField(Fields[Thrift10].Column1)
    def getId(x: Thrift10) = x.column1
    def setId(x: Thrift10, i: Int) = { x.column1 = i }
  }

  implicit object ThriftN100 extends ThriftN[Thrift100] {
    def init: Thrift100 = new Thrift100
    def partition = Partition.byField(Fields[Thrift100].Column1)
    def getId(x: Thrift100) = x.column1
    def setId(x: Thrift100, i: Int) = { x.column1 = i }
  }

  implicit object ThriftN250 extends ThriftN[Thrift250] {
    def init: Thrift250 = new Thrift250
    def partition = Partition.byField(Fields[Thrift250].Column1)
    def getId(x: Thrift250) = x.column1
    def setId(x: Thrift250, i: Int) = { x.column1 = i }
  }

  implicit object ThriftN500 extends ThriftN[Thrift500] {
    def init: Thrift500 = new Thrift500
    def partition = Partition.byField(Fields[Thrift500].Column1)
    def getId(x: Thrift500) = x.column1
    def setId(x: Thrift500, i: Int) = { x.column1 = i }
  }

}


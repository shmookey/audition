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

import scala.concurrent._
import ExecutionContext.Implicits.global

import scalaz._, Scalaz._

import org.apache.hadoop.hive.conf.HiveConf

import com.twitter.scrooge.ThriftStruct
import com.twitter.scalding.{Config, Execution}
import com.twitter.scalding.typed.TypedPipe

import au.com.cba.omnia.ebenezer.scrooge.hive.Hive

import au.com.cba.omnia.omnitool.{Result, Ok, Error}

import au.com.cba.omnia.maestro.api._, Maestro._
import au.com.cba.omnia.maestro.scalding.ConfHelper

import au.com.cba.omnia.audition.thrift.{Thrift10, Thrift100, Thrift250, Thrift500}

/** Configuration for a customer execution example */
case class AuditionJobConfig[T <: ThriftStruct : ThriftN : Manifest](config: Config) {
  val maestro = MaestroConfig(
    conf      = config, 
    source    = "test",
    domain    = "test",
    tablename = "test"
  )
  val nPartitions   = config.getArgs.int("num-partitions")
  val partitionSize = config.getArgs.int("partition-size")
  val batchSize     = config.getArgs.int("batch-size")
  val withQueries   = config.getArgs.boolean("with-queries")
  
  val table = maestro.partitionedHiveTable[T, Int](
    partition = implicitly[ThriftN[T]].partition,
    tablename = "by_col1",
    path      = Some(maestro.hdfsRoot)
  )
}

object AuditionJob extends MaestroJob {

  case class AuditionWorker[T <: ThriftStruct : ThriftN : Manifest] (conf: AuditionJobConfig[T]) {
    // todo: actually use partition size (this code effectively defaults to 1)
    def generateRecords(n: Int, offset: Int, partitionSize: Int): Stream[T] =
      Stream.from(offset)
        .map(ThriftN.create(_))
        .take(n)
        
    def createPipe(n: Int, offset: Int, partitionSize: Int): TypedPipe[T] =
      TypedPipe.from(generateRecords(n, offset, partitionSize))
        .groupBy(ThriftN.id(_))
        .forceToReducers
        .map{ case (k,v) => v }

    def createExecutions: Stream[Execution[Long]] = {
      val nRecords  = conf.partitionSize * conf.nPartitions
      val batchSize = math.min(conf.batchSize, nRecords)
      Stream.from(0, batchSize)
        .map(createPipe(batchSize, _, conf.partitionSize))
        .map(viewHive(conf.table, _))
        .take(nRecords / batchSize)
    }
  
    def spawnQueryThread: Unit = if (!conf.withQueries) () else {
      logger.info("Starting query loop.")
      val hiveConf = new HiveConf(ConfHelper.getHadoopConf(conf.config), this.getClass)
      val queriesTask = Future{
        while(true) {
          val query = s"SELECT * FROM ${conf.table.name}"
          logger.info(s"Running query: $query")
          Hive.query(query).run(hiveConf) match {
            case Ok(r)    => logger.info(s"Query returned: $r")
            case Error(e) => logger.error(s"Failed to execute query: $e")
          }
          Thread.sleep(5000)
        }
      }
      ()
    }
  }
 

  def job: Execution[JobStatus] = for {
    conf  <- Execution.getConfig.map(AuditionJobConfig[Thrift10](_))
    worker = AuditionWorker(conf)
    _     <- Execution.value(worker.spawnQueryThread)
    _     <- worker.createExecutions.sequence
  } yield JobFinished

  def attemptsExceeded = Execution.from(JobNeverReady)   // Elided in the README
}


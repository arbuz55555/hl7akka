package io.hl7akka.core

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import io.hl7akka.core.actor.HL7Server

import scala.concurrent.duration._

import spray.can.Http
/**
* Copyright 2011-2014 Lukasz Sztygiel 5x5 Solutions LTD (www.5x5sols.com)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
object Main extends App {

  val config = ConfigFactory.load()

  val host = config.getString("hl7akka.host")

  val port = config.getInt("hl7akka.port")

  implicit val system = ActorSystem("hl7akka")

  val api = system.actorOf(Props(new HL7Server()), "hl7server")

  implicit val executionContext = system.dispatcher

  implicit val timeout = Timeout(10 seconds)

  IO(Http).ask(Http.Bind(listener = api, interface = host, port = port))
    .mapTo[Http.Event]
    .map {
    case Http.Bound(address) =>
      println(s"HL7 server bound to $address")
    case Http.CommandFailed(cmd) =>
      println("HL7 server could not bind to " +
        s"$host:$port, ${cmd.failureMessage}")
      system.shutdown()
  }
}

package io.hl7akka.core

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import io.hl7akka.core.actor.HL7Server

import scala.concurrent.duration._

import spray.can.Http

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

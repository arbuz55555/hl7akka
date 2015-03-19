package io.hl7akka.core.actor

import akka.actor._
import spray.http.StatusCodes
import spray.routing._
import akka.util.Timeout
import scala.concurrent.duration._

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

class HL7Server extends HttpServiceActor
                  with HL7ServerApi {
  def receive = runRoute(routes)
}

trait HL7ServerApi extends HttpService with ActorLogging {
  actor: Actor =>

  import context.dispatcher
  import akka.pattern.ask
  import akka.pattern.pipe
  import io.hl7akka.core.actor.HL7MessageProtocol._

  implicit val timeout = Timeout(10 seconds)

  val processor = context.system.actorOf(Props(new HL7Processor()))

  def routes: Route =

    path("adt" / IntNumber) { adtVersion =>
      post {
        entity(as[AdtMessage]) { adtMessage: AdtMessage => requestContext: RequestContext =>
          val responder = createHl7Responder(requestContext)
          log.info(s"Receiving adt message ${adtMessage}.")
          processor.ask(AdtMessageVersioned(adtMessage, adtVersion.toString)).pipeTo(responder)
        }
      }
    } ~
    path("obs") {
      post {
        entity(as[ObsMessage]) { obsMessage: ObsMessage => requestContext: RequestContext =>
          val responder = createHl7Responder(requestContext)
          log.info(s"Receiving obs message ${obsMessage}.")
          processor.ask(obsMessage).pipeTo(responder)
        }
      }
    }

  def createHl7Responder(requestContext: RequestContext) = {
    context.actorOf(Props(new HL7Responder(requestContext)))
  }
}

object HL7MessageWorkflowProtocol {

  case object HL7MessageAccepted
  case object HL7MessageInvalid
  case object HL7MessageInvalidEncoding

}

class HL7Responder(requestContext: RequestContext) extends Actor with ActorLogging {

  import io.hl7akka.core.actor.HL7MessageWorkflowProtocol._

  def receive = {

    case HL7MessageAccepted =>
      log.info("HL7 Message accepted")
      requestContext.complete(StatusCodes.OK)
      self ! PoisonPill

    case HL7MessageInvalid | HL7MessageInvalidEncoding =>
      log.info("HL7 Message invalid")
      requestContext.complete(StatusCodes.BadRequest)
      self ! PoisonPill
  }
}
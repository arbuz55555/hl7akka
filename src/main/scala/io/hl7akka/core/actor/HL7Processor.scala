package io.hl7akka.core.actor

import akka.actor.{Actor, ActorLogging}
import ca.uhn.hl7v2.{HL7Exception, DefaultHapiContext}
import ca.uhn.hl7v2.model.Message
import ca.uhn.hl7v2.parser.EncodingNotSupportedException
import scala.concurrent.duration._
import akka.util.Timeout

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
class HL7Processor extends Actor with ActorLogging {

  import io.hl7akka.core.actor.HL7MessageProtocol._
  import io.hl7akka.core.actor.HL7MessageWorkflowProtocol._

  implicit val timeout = Timeout(5 seconds)

  def receive = {

    case AdtMessage(data, version) =>
      val hapiContext = new DefaultHapiContext()
      val parser = hapiContext.getGenericParser()

      var hpiMsg:Message = null

      try {
        hpiMsg = parser.parse(data)
        sender ! HL7MessageAccepted
      } catch {
        case e:EncodingNotSupportedException =>
          sender ! HL7MessageInvalidEncoding
        case e1:HL7Exception =>
          sender ! HL7MessageInvalid
      }

    case CustomMessage(data) =>
      log.info("Custom processor")

  }
}

object HL7MessageProtocol {

  import spray.httpx.unmarshalling._
  import spray.http._

  trait HL7Message {
    val data: String
    override def toString = data
  }


  val `x-application/hl7-v2+er7` =
    MediaTypes.register(MediaType.custom("x-application/hl7-v2+er7"))

  val `application/hl7-v2` =
    MediaTypes.register(MediaType.custom("application/hl7-v2"))

  implicit val standardMessageUnmarshaller =
    Unmarshaller[StandardMessage](`x-application/hl7-v2+er7`, `application/hl7-v2`) {
      case HttpEntity.NonEmpty(contentType, data) =>
        StandardMessage(data.asString)
    }

  implicit val customMessageUnmarshaller =
    Unmarshaller[CustomMessage](`x-application/hl7-v2+er7`, `application/hl7-v2`) {
      case HttpEntity.NonEmpty(contentType, data) =>
        CustomMessage(data.toString)

    }

  case class StandardMessage(override val data: String) extends HL7Message

  case class CustomMessage(override val data: String) extends HL7Message

  case class AdtMessage(override val data: String, adtVersion: String) extends HL7Message

}


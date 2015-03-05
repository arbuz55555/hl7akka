package io.hl7akka.core.actor

import akka.actor.{ActorRef, Actor}

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
class HL7Receiver extends Actor {
  import HL7ReceiverProtocol._

  var internalState = Vector[String]()

  def receive() = {
    case HL7Message(data) =>
      internalState = internalState :+ data
    case GetState(receiver) =>
      receiver ! internalState
  }

  def state = internalState

}

object HL7ReceiverProtocol {
  case class HL7Message(data: String)
  case class GetState(receiver: ActorRef)
}
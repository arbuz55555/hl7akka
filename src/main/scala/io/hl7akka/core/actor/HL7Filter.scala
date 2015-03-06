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
class HL7Filter(nextActor: ActorRef, bufferSize: Int) extends Actor {

  import HL7FilterProtocol._

  var lastMessages = Vector[HL7Message]()

  def receive = {
    case msg: HL7Message =>
      if (!lastMessages.contains(msg)) {
        lastMessages = lastMessages :+ msg
        nextActor ! msg
        if (lastMessages.size > bufferSize)
          lastMessages = lastMessages.tail
      }
  }

}

object HL7FilterProtocol {
  case class HL7Message(id: Long)
}

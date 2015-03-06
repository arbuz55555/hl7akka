package io.hl7akka.core

import akka.actor.{Props, ActorSystem}
import akka.testkit.TestKit
import org.scalatest.{MustMatchers, WordSpecLike}

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
class HL7FilterSpec extends TestKit(ActorSystem("hl7filter"))
                      with WordSpecLike
                      with MustMatchers
                      with StopSystemAfterAll {

  "filter out particular messages" in {
    import io.hl7akka.core.actor.HL7Filter
    import io.hl7akka.core.actor.HL7FilterProtocol._

    val props = Props(new HL7Filter(testActor, 5))
    val filter = system.actorOf(props)

    filter ! HL7Message(1)
    filter ! HL7Message(2)
    filter ! HL7Message(1)
    filter ! HL7Message(3)
    filter ! HL7Message(1)
    filter ! HL7Message(4)
    filter ! HL7Message(5)
    filter ! HL7Message(5)
    filter ! HL7Message(6)

    val messagesIds = receiveWhile() {
      case HL7Message(id) if (id <= 5) => id
    }

    messagesIds must be(List(1, 2, 3, 4, 5))
    expectMsg(HL7Message(6))
  }
}


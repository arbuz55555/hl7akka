package io.hl7akka.core

import akka.testkit.{ImplicitSender, TestKit}
import akka.actor.{Actor, ActorRef, Props, ActorSystem}
import org.scalatest.{WordSpecLike, MustMatchers}
import io.hl7akka.core.actor._


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
class HL7ReceiverSpec extends TestKit(ActorSystem("hl7parser"))
                        with WordSpecLike
                        with MustMatchers
                        with StopSystemAfterAll {

  "The HL7Receiver" must {

    "change internal state when it receives message, multi" in {

      import HL7ReceiverProtocol._

      val hl7ReceiverActor = system.actorOf(Props[HL7Receiver], "hl7receiver")
      hl7ReceiverActor ! HL7Message("msg1")
      hl7ReceiverActor ! HL7Message("msg2")
      hl7ReceiverActor ! GetState(testActor)
      expectMsg(Vector("msg1", "msg2"))
    }
  }
}

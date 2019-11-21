package by.zmitserkoskinen.spring.statemachineexample.config

import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentEvent
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentEvent.PRE_AUTHORIZE
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentEvent.PRE_AUTH_APPROVED
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentStatus
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.statemachine.config.StateMachineFactory

@SpringBootTest
internal class StateMachineConfigTest(@Autowired val factory: StateMachineFactory<PaymentStatus, PaymentEvent>) {

    @Test
    fun testNewStateMachine() {
        val stateMachine = factory.stateMachine
        stateMachine.start()
        println(stateMachine.state)

        stateMachine.sendEvent(PRE_AUTHORIZE)
        println(stateMachine.state)

        stateMachine.sendEvent(PRE_AUTH_APPROVED)
        println(stateMachine.state)
    }

}
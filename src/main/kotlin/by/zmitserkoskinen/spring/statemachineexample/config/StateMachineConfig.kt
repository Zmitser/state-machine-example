package by.zmitserkoskinen.spring.statemachineexample.config

import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentEvent
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentEvent.*
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentStatus
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentStatus.*
import org.springframework.context.annotation.Configuration
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer
import java.util.*

@EnableStateMachineFactory
@Configuration
class StateMachineConfig : EnumStateMachineConfigurerAdapter<PaymentStatus, PaymentEvent>() {

    override fun configure(states: StateMachineStateConfigurer<PaymentStatus, PaymentEvent>?) {
        states?.withStates()
                ?.initial(NEW)
                ?.states(EnumSet.allOf(PaymentStatus::class.java))
                ?.end(AUTH)
                ?.end(AUTH_ERROR)
                ?.end(PRE_AUTH_ERROR)
    }

    override fun configure(transitions: StateMachineTransitionConfigurer<PaymentStatus, PaymentEvent>?) {
        transitions?.withExternal()?.source(NEW)?.target(NEW)?.event(PRE_AUTHORIZE)
                ?.and()
                ?.withExternal()?.source(NEW)?.target(PRE_AUTH)?.event(PRE_AUTH_APPROVED)
                ?.and()
                ?.withExternal()?.source(NEW)?.target(PRE_AUTH_ERROR)?.event(PRE_AUTH_DECLINED)
    }
}
package by.zmitserkoskinen.spring.statemachineexample.config

import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentEvent
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentEvent.*
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentStatus
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentStatus.*
import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.action.Action
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.StateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer
import org.springframework.statemachine.listener.StateMachineListenerAdapter
import org.springframework.statemachine.state.State
import java.util.*
import kotlin.random.Random


@EnableStateMachineFactory
@Configuration
class StateMachineConfig : StateMachineConfigurerAdapter<PaymentStatus, PaymentEvent>() {
    private val PAYMENT_ID_HEADER = "paymentId"

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
                ?.action(preAuthAction())
                ?.and()
                ?.withExternal()?.source(NEW)?.target(PRE_AUTH)?.event(PRE_AUTH_APPROVED)
                ?.and()
                ?.withExternal()?.source(NEW)?.target(PRE_AUTH_ERROR)?.event(PRE_AUTH_DECLINED)
    }

    override fun configure(config: StateMachineConfigurationConfigurer<PaymentStatus, PaymentEvent>?) {
        val adapter = LoggerStateMachineListenerAdapter()
        config?.withConfiguration()?.listener(adapter)

    }


    class LoggerStateMachineListenerAdapter : StateMachineListenerAdapter<PaymentStatus, PaymentEvent>() {
        private val log = KotlinLogging.logger { }

        override fun stateChanged(from: State<PaymentStatus, PaymentEvent>?, to: State<PaymentStatus, PaymentEvent>?) {
            log.info("State Changed from $from to $to")
        }
    }


    fun preAuthAction(): Action<PaymentStatus, PaymentEvent> {
        return Action {
            println("PreAuth was called!!!")
            if (Random.nextInt(20) > 8) {
                println("Approved")
                it.stateMachine.sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
                        .setHeader(PAYMENT_ID_HEADER, it.getMessageHeader(PAYMENT_ID_HEADER))
                        .build())
            } else {
                println("Declined! No Credit!!!!!!");
                it.stateMachine.sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
                        .setHeader(PAYMENT_ID_HEADER, it.getMessageHeader(PAYMENT_ID_HEADER))
                        .build())
            }
        }
    }
}
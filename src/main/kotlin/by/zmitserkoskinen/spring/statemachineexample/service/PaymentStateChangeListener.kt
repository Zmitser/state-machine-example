package by.zmitserkoskinen.spring.statemachineexample.service

import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentEvent
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentStatus
import by.zmitserkoskinen.spring.statemachineexample.repository.PaymentRepository
import org.springframework.messaging.Message
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.state.State
import org.springframework.statemachine.support.StateMachineInterceptorAdapter
import org.springframework.statemachine.transition.Transition
import org.springframework.stereotype.Component

@Component
class PaymentStateChangeListener(private val repository: PaymentRepository) : StateMachineInterceptorAdapter<PaymentStatus, PaymentEvent>() {

    private val PAYMENT_ID_HEADER = "paymentId"

    override fun preStateChange(state: State<PaymentStatus, PaymentEvent>?,
                                message: Message<PaymentEvent>?,
                                transition: Transition<PaymentStatus, PaymentEvent>?,
                                stateMachine: StateMachine<PaymentStatus, PaymentEvent>?) {

        val paymentId = message?.headers?.getOrDefault(PAYMENT_ID_HEADER, 1L).toString().toLong()
        val payment = repository.getOne(paymentId)
        payment.state = state?.id
        repository.save(payment)
    }
}
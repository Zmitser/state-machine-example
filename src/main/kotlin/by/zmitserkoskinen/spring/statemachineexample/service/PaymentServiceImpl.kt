package by.zmitserkoskinen.spring.statemachineexample.service

import by.zmitserkoskinen.spring.statemachineexample.domain.Payment
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentEvent
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentEvent.*
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentStatus
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentStatus.NEW
import by.zmitserkoskinen.spring.statemachineexample.repository.PaymentRepository
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.action.Action
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.statemachine.support.DefaultStateMachineContext
import org.springframework.stereotype.Service

@Service
class PaymentServiceImpl(private val repository: PaymentRepository,
                         private val factory: StateMachineFactory<PaymentStatus, PaymentEvent>,
                         private val listener: PaymentStateChangeListener) : PaymentService {

    private val PAYMENT_ID_HEADER = "paymentId"

    override fun new(payment: Payment): Payment {
        payment.state = NEW
        return repository.save(payment)
    }

    override fun preAuth(id: Long): StateMachine<PaymentStatus, PaymentEvent> {
        val stateMachine = build(id)
        sendEvent(id, stateMachine, PRE_AUTHORIZE)
        return stateMachine
    }

    override fun authorize(id: Long): StateMachine<PaymentStatus, PaymentEvent> {
        val stateMachine = build(id)
        sendEvent(id, stateMachine, AUTH_APPROVED)
        return stateMachine
    }

    override fun decline(id: Long): StateMachine<PaymentStatus, PaymentEvent> {
        val stateMachine = build(id)
        sendEvent(id, stateMachine, AUTH_DECLINED)

        return stateMachine
    }


    private fun sendEvent(id: Long,
                          stateMachine: StateMachine<PaymentStatus, PaymentEvent>,
                          paymentEvent: PaymentEvent) {
        val message = MessageBuilder.withPayload(paymentEvent)
                .setHeader(PAYMENT_ID_HEADER, id)
                .build()
        stateMachine.sendEvent(message)
    }


    fun build(id: Long): StateMachine<PaymentStatus, PaymentEvent> {
        val (existedId, state, _) = repository.getOne(id)
        val stateMachine = factory.getStateMachine(existedId.toString())
        stateMachine.stop()

        stateMachine.stateMachineAccessor.doWithAllRegions {
            it.addStateMachineInterceptor(listener)
            it.resetStateMachine(DefaultStateMachineContext<PaymentStatus, PaymentEvent>(state, null, null, null))
        }

        stateMachine.start()

        return stateMachine

    }

}
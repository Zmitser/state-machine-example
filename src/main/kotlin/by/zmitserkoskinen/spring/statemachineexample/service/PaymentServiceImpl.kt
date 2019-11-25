package by.zmitserkoskinen.spring.statemachineexample.service

import by.zmitserkoskinen.spring.statemachineexample.domain.Payment
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentEvent
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentEvent.*
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentStatus
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentStatus.NEW
import by.zmitserkoskinen.spring.statemachineexample.repository.PaymentRepository
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.statemachine.support.DefaultStateMachineContext
import org.springframework.stereotype.Service

@Service
class PaymentServiceImpl(private val repository: PaymentRepository,
                         private val factory: StateMachineFactory<PaymentStatus, PaymentEvent>) : PaymentService {

    private val PAYMENT_ID_HEADER = "paymentId"

    override fun new(payment: Payment): Payment {
        payment.state = NEW
        return repository.save(payment)
    }

    override fun preAuth(id: Long): StateMachine<PaymentStatus, PaymentEvent> {
        val stateMachine = build(id)
        sendEvent(id, stateMachine, PRE_AUTHORIZE)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun authorize(id: Long): StateMachine<PaymentStatus, PaymentEvent> {
        val stateMachine = build(id)
        sendEvent(id, stateMachine, AUTH_APPROVED)

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decline(id: Long): StateMachine<PaymentStatus, PaymentEvent> {
        val stateMachine = build(id)
        sendEvent(id, stateMachine, AUTH_DECLINED)

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
            it.resetStateMachine(DefaultStateMachineContext<PaymentStatus, PaymentEvent>(state, null, null, null))
        }

        stateMachine.start()

        return stateMachine

    }
}
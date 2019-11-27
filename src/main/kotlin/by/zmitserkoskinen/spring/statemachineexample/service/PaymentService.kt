package by.zmitserkoskinen.spring.statemachineexample.service

import by.zmitserkoskinen.spring.statemachineexample.domain.Payment
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentEvent
import by.zmitserkoskinen.spring.statemachineexample.domain.PaymentStatus
import org.springframework.statemachine.StateMachine

interface PaymentService {

    fun new(payment: Payment): Payment

    fun authorize(id: Long): StateMachine<PaymentStatus, PaymentEvent>

    fun decline(id: Long): StateMachine<PaymentStatus, PaymentEvent>
    fun preAuth(id: Long): StateMachine<PaymentStatus, PaymentEvent>
}
package by.zmitserkoskinen.spring.statemachineexample.repository

import by.zmitserkoskinen.spring.statemachineexample.domain.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository : JpaRepository<Payment, Long> {
}
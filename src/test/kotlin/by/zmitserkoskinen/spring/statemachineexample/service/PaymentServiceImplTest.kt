package by.zmitserkoskinen.spring.statemachineexample.service

import by.zmitserkoskinen.spring.statemachineexample.domain.Payment
import by.zmitserkoskinen.spring.statemachineexample.repository.PaymentRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
internal class PaymentServiceImplTest(@Autowired val service: PaymentService,
                                      @Autowired val repository: PaymentRepository) {

    private lateinit var payment: Payment
    @BeforeEach
    fun setUp() {
        payment = Payment(1L, null, BigDecimal("12.85"))
    }

    @Test
    fun preAuth() {
        val (id, state, amount) = service.new(payment)
        service.preAuth(id)
        val one = repository.getOne(id)
        println(one)
    }
}
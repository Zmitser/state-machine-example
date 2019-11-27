package by.zmitserkoskinen.spring.statemachineexample.domain

import java.math.BigDecimal
import javax.persistence.*

@Entity
data class Payment(@Id @GeneratedValue var id: Long, @Enumerated(EnumType.STRING) var state: PaymentStatus?, var amount: BigDecimal)
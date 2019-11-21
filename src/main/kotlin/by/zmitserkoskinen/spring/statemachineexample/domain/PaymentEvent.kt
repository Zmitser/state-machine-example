package by.zmitserkoskinen.spring.statemachineexample.domain

enum class PaymentEvent {

    PRE_AUTHORIZE, PRE_AUTH_APPROVED, PRE_AUTH_DECLINED, AUTHORIZE, AUTH_APPROVED, AUTH_DECLINED
}
package com.cabbooking.paymentservice;

import com.cabbooking.paymentservice.controller.PaymentControllerTests;
import com.cabbooking.paymentservice.service.PaymentServiceTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        PaymentServiceTests.class,
        PaymentControllerTests.class
})
class PaymentserviceApplicationTests {


}

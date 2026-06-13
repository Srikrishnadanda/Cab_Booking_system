package com.cabbooking.rideservice;

import com.cabbooking.rideservice.controller.RideControllerTest;
import com.cabbooking.rideservice.serviceimpl.RideServiceImplTest;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Suite
@SelectClasses({
		RideServiceImplTest.class,
		RideControllerTest.class
})
class RideserviceApplicationTests {

}

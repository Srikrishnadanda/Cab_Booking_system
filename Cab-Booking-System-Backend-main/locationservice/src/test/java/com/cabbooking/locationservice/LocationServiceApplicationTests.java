package com.cabbooking.locationservice;

import com.cabbooking.locationservice.controller.LocationControllerTests;
import com.cabbooking.locationservice.service.LocationServiceTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Suite
@SelectClasses({
        LocationServiceTests.class,
        LocationControllerTests.class
})
public class LocationServiceApplicationTests {

}
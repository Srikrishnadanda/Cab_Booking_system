package com.cabbooking.ratingservice;

import com.cabbooking.ratingservice.controller.RatingControllerTests;
import com.cabbooking.ratingservice.service.RatingServiceTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Suite
@SelectClasses({
        RatingServiceTests.class,
        RatingControllerTests.class
})
class RatingServiceApplicationTests {

}

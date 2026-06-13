package com.cabbooking.userservice;



import com.cabbooking.userservice.controller.UserControllerTest;
import com.cabbooking.userservice.service.UserServiceImplTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Suite
@SelectClasses({
		UserControllerTest.class,
		UserServiceImplTest.class
	}
)
class UserserviceApplicationTests {

}

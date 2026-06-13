package com.cabbooking.authservice;

import com.cabbooking.authservice.serviceimpl.AuthenticationServiceImplTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Suite
@SelectClasses({
        AuthenticationServiceImplTest.class
})
class AuthserviceApplicationTests {


}

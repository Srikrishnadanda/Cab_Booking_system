package com.cabbooking;

import com.cabbooking.controller.DriverControllerTests;
import com.cabbooking.serviceimpl.DriverServiceImplTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Driver Service Complete Test Suite")
@SelectClasses({
    DriverServiceImplTest.class,
    DriverControllerTests.class
})
@DisplayName("Driver Service - Complete Test Suite (Service + Controller)")
public class DriverTestSuite {
}

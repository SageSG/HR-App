package com.example.nodrama

import com.example.nodrama.view.*
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(AccountActivityTest::class,
                    ApplyLeaveActivityTest::class,
                    ARTActivityTest::class,
                    LoginActivityTest::class,
                    MainActivityTest::class,
                    MainLeaveActivityTest::class,
                    NavBarActivityTest::class,
                    NFCActivityTest::class,
                    PayslipActivityTest::class,
                    ResetPasswordActivityTest::class,
                    TimesheetActivityTest::class)
class ActivityTestSuite
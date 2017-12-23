package com.smi.mc.api.common;

import com.smi.mc.api.MemberAPIServiceApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MemberAPIServiceApplication.class)
@WebIntegrationTest(value = { "spring.profiles.active=development" })
public class BaseWebIntegrationTests {

}

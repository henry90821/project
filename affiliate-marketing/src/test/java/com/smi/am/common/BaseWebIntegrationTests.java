package com.smi.am.common;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.smi.am.AffiliateMarketingApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AffiliateMarketingApplication.class)
@WebIntegrationTest(value = { "spring.profiles.active=development" })
public class BaseWebIntegrationTests {

}

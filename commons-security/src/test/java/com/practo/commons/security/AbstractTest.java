package com.practo.commons.security;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import com.practo.commons.config.Profiles;

@ActiveProfiles(Profiles.TESTING)
@SpringBootTest(classes = { TestApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public abstract class AbstractTest {

}

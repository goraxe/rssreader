package com.github.goraxe.rss_reader.it;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.WebIntegrationTest;

/**
 * Created by goraxe on 2016-05-02.
 * Boo Yar
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features")
public class CucumberTest extends SpringTestBase {

    CucumberTest() {
        super();
    }

    @Test
    public void thiIsNotReal() throws Exception {
        throw new Exception();
    }


}

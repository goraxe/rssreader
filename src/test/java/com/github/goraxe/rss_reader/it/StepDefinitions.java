package com.github.goraxe.rss_reader.it;

import cucumber.api.PendingException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.hamcrest.core.IsEqual;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by goraxe on 2016-05-02.
 * Boo Yar
 */
public class StepDefinitions extends SpringTestBase {

    @When("^the client calls (/.*)$")
    public void the_client_calls(String url) throws Throwable {
        executeGet(url);
    }

    @Then("^the client recieves status code (\\d+)$")
    public void the_client_recieves_status_code(int code) throws Throwable {
        assertTrue(lastResult.getClientHttpResponse().getStatusCode().value() == code);
    }

}

package com.github.goraxe.rss_reader.it;

import com.github.goraxe.rss_reader.App;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by goraxe on 2016-05-02.
 * Boo Yar
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = App.class, loader = SpringApplicationContextLoader.class)
@WebIntegrationTest("sever.port:8080")
public class SpringTestBase {


    @Value("${local.server.port}")
    int port;

    protected RestTemplate restTemplate = null;

    protected HeaderSettingRequestCallback requestCallback;

    protected ResponseResults lastResult;

    protected void executeGet(String url) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

        requestCallback = new HeaderSettingRequestCallback(headers);

        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }

        ResponseResultErrorHandler errorHandler = new ResponseResultErrorHandler();
        restTemplate.setErrorHandler(errorHandler);


        lastResult = restTemplate.execute("http://localhost" + ":" + port + url,
                HttpMethod.GET,
                requestCallback,
                new ResponseExtractor<ResponseResults>() {
                    @Override
                    public ResponseResults extractData(ClientHttpResponse clientHttpResponse) throws IOException {

                        if (errorHandler.hasError(clientHttpResponse)) {
                            return errorHandler.getResults();
                        }

                        return (new ResponseResults(clientHttpResponse));
                    }
                }


        );
    }

    private class ResponseResultErrorHandler implements ResponseErrorHandler {

        private ResponseResults results;
        private Boolean hadError = false;

        @Override
        public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
            hadError = clientHttpResponse.getRawStatusCode() >= 400;
            return hadError;
        }

        @Override
        public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
            results = new ResponseResults(clientHttpResponse);
        }

        public ResponseResults getResults() {
            return results;
        }
    }

    /**
     * Created by goraxe on 2016-05-02.
     * Boo Yar
     */
    protected class ResponseResults {

        private ClientHttpResponse clientHttpResponse;
        private String body;

        public ResponseResults(ClientHttpResponse clientHttpResponse) throws IOException {
            this.clientHttpResponse = clientHttpResponse;
            InputStream bodyInputStream = clientHttpResponse.getBody();

            if (bodyInputStream == null) {
                this.body = "{}";
            }
            else {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                IOUtils.copy(bodyInputStream, outputStream);
                // TODO handle diffenent encodings
                this.body = new String(outputStream.toByteArray());
            }
        }

        public String getBody() {
            return body;
        }

        public ClientHttpResponse getClientHttpResponse() {
            return clientHttpResponse;
        }
    }
}

/*
 * The MIT License
 *
 * Copyright 2022 Catchpoint Systems.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.jenkins.plugins;

import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.HttpResponseException;

public class RequestMaker {

    public static final String BASE_URL = "https://io.catchpoint.com/ui/api";
    public static final String API_VERSION = "/v1";
    public static final String ENDPOINT_TOKEN = "/token";
    public static final String ONDEMAND_PATH = "/onDemandTest/";
    public static final String INSTANT_PATH = "/InstantTest/";
    public static final String NEWTEST_ID = "0";

    // POSTs the Catchpoint API credentials and returns the response status and response body containing token
    public static String[] getToken(String apiKey, String apiSecret) throws IOException {
        String[] result = new String[2];
        String postData = "grant_type=client_credentials&client_id=" + apiKey + "&client_secret=" + apiSecret;
        final String URL = BASE_URL + ENDPOINT_TOKEN;
        HttpPost post = new HttpPost(URL);
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
        post.addHeader("Accept", "*/*");
        post.setEntity(new StringEntity(postData, UTF_8));

        try ( CloseableHttpClient httpClient = HttpClients.createDefault();  CloseableHttpResponse response = httpClient.execute(post)) {
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() != 200) {
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
            result[0] = statusLine.toString();
            result[1] = EntityUtils.toString(response.getEntity(), UTF_8);
        }
        return result;
    }

    // POSTs the JSON payload for the test and returns the response status and response body containing the test id
    public static String[] postTest(String apiToken, String apiRequestBody, Boolean isUseInstantTest) throws IOException {
        String[] result = new String[2];
        String tokenHeader = "Bearer " + apiToken;
        String postData = apiRequestBody;
        final String TEST_PATH = (isUseInstantTest) ? INSTANT_PATH : ONDEMAND_PATH;
        final String URL = BASE_URL + API_VERSION + TEST_PATH + NEWTEST_ID;
        HttpPost post = new HttpPost(URL);
        post.addHeader("Content-Type", "application/json");
        post.addHeader("Accept", "*/*");
        post.addHeader("Authorization", tokenHeader);
        post.setEntity(new StringEntity(postData, UTF_8));

        try ( CloseableHttpClient httpClient = HttpClients.createDefault();  CloseableHttpResponse response = httpClient.execute(post)) {
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() != 200) {
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
            result[0] = statusLine.toString();
            result[1] = EntityUtils.toString(response.getEntity(), UTF_8);
        }
        return result;
    }

    // GETs the test data using the test Id and returns the response status and response body containing the test result
    // Synchronized method
    public static synchronized String[] getTestResult(String apiToken, String testId, Boolean isUseInstantTest) throws IOException {
        String[] result = new String[2];
        String tokenHeader = "Bearer " + apiToken;
        final String TEST_PATH = (isUseInstantTest) ? INSTANT_PATH : ONDEMAND_PATH;
        final String URL = BASE_URL + API_VERSION + TEST_PATH + testId;
        HttpGet get = new HttpGet(URL);
        get.addHeader("Accept", "*/*");
        get.addHeader("Authorization", tokenHeader);

        try ( CloseableHttpClient httpClient = HttpClients.createDefault();  CloseableHttpResponse response = httpClient.execute(get)) {
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() != 200) {
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
            result[0] = statusLine.toString();
            result[1] = EntityUtils.toString(response.getEntity(), UTF_8);
        }
        return result;
    }
}

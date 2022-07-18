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

import org.apache.http.client.HttpResponseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class RequestMakerTest {

    public RequestMakerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getToken method, of class RequestMaker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetTokenWithBadCredentials() throws Exception {
        String apiKey = "badKey";
        String apiSecret = "badSecret";
        String expMessage = "Bad Request";
        Exception exception = assertThrows(HttpResponseException.class, () -> {
            RequestMaker.getToken(apiKey, apiSecret);
        });
        String result = exception.getMessage();
        assertTrue(result.contains(expMessage));
    }

    /**
     * Test of postTest method, of class RequestMaker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testPostTestWithInvalidToken() throws Exception {
        String apiToken = "RytEUWR1bE9xRW5rdEowSEJwOG5yU2Jrblp2YXd1OFViWTA5MXp2R1lSUy9Pa3dqKy9TVTdOSW5XN2lwT3FHWjBqTXMwU1h4MlUxSWhUMnZsRk5ZTWRqU1lzSEZUS204a2VZR2pUNENDcy9xT1dtZmxZWFJ4UXVDK2NCemFZTnc1TFBrNlk0V3lTSEdDSmNRdnVIRU9XVnV3N3BvYk40TSsrUWNKdmxqZDI2eUVZRHUzYUFacEFsK2tUOThDN2lFczQ2VXdyZjMvZDY4L25vK1FlWllXdGZpTkRoUGJnQUJ3bXEzaGMvU3hwdFZWZENC";
        String apiRequestBody = "{ \"http_method\": { \"name\": \"GET\", \"id\": 0 }, \"id\": 0, \"test_type\": { \"name\": \"Web\", \"id\": 0 }, \"monitor\": { \"id\": 2, \"name\": \"Object\" }, \"test_url\": \"https://www.google.com\", \"advanced_settings\": { \"additional_settings\": { \"capture_http_headers\": false, \"ignore_ssl_failures\": false, \"treat_40X_or_50X_http_response_as_successful_test_run\": false } }, \"nodes\": [ { \"name\": \"New York, US - Level3\", \"id\": 11 } ] }";
        Boolean isUseInstantTest = false;
        String expMessage = "Unauthorized";
        Exception exception = assertThrows(HttpResponseException.class, () -> {
            RequestMaker.postTest(apiToken, apiRequestBody, isUseInstantTest);
        });
        String result = exception.getMessage();
        assertTrue(result.contains(expMessage));
    }

    /**
     * Test of getTestResult method, of class RequestMaker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetTestResultWithInvalidToken() throws Exception {
        String apiToken = "RytEUWR1bE9xRW5rdEowSEJwOG5yU2Jrblp2YXd1OFViWTA5MXp2R1lSUy9Pa3dqKy9TVTdOSW5XN2lwT3FHWjBqTXMwU1h4MlUxSWhUMnZsRk5ZTWRqU1lzSEZUS204a2VZR2pUNENDcy9xT1dtZmxZWFJ4UXVDK2NCemFZTnc1TFBrNlk0V3lTSEdDSmNRdnVIRU9XVnV3N3BvYk40TSsrUWNKdmxqZDI2eUVZRHUzYUFacEFsK2tUOThDN2lFczQ2VXdyZjMvZDY4L25vK1FlWllXdGZpTkRoUGJnQUJ3bXEzaGMvU3hwdFZWZENC";
        String testId = "34567";
        Boolean isUseInstantTest = false;
        String expMessage = "Unauthorized";
        Exception exception = assertThrows(HttpResponseException.class, () -> {
            RequestMaker.getTestResult(apiToken, testId, isUseInstantTest);
        });
        String result = exception.getMessage();
        assertTrue(result.contains(expMessage));
    }

}

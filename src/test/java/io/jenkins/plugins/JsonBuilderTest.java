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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class JsonBuilderTest {

    public JsonBuilderTest() {
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

    Integer nodeId = 136;
    String nodeName = "Washington DC, US - Cogent";

    /**
     * Test of createWebTestJson method, of class JsonBuilder.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateWebTestJson() throws Exception {
        String webTestUrl = "https://www.example.com";
        JsonBuilder instance = new JsonBuilder(nodeId, nodeName);
        String expResult = "{\"http_method\":{\"name\":\"GET\",\"id\":0},\"id\":0,\"test_type\":{\"name\":\"Web\",\"id\":0},\"monitor\":{\"name\":\"Chrome\",\"id\":18},\"test_url\":\"https://www.example.com\",\"advanced_settings\":{\"additional_settings\":{\"capture_test_end_screenshot\":true,\"capture_http_headers\":true,\"capture_filmstrip\":true,\"ignore_ssl_failures\":false,\"treat_40X_or_50X_http_response_as_successful_test_run\":false}},\"nodes\":[{\"name\":\"Washington DC, US - Cogent\",\"id\":136}]}";
        String result = instance.createWebTestJson(webTestUrl);
        assertEquals(expResult, result);
    }

    /**
     * Test of createTransactionTestJson method, of class JsonBuilder.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateTransactionTestJson() throws Exception {
        String transactionTestScript = "open(\"www.google.com\")\nopen(\"www.yahoo.com\")";
        JsonBuilder instance = new JsonBuilder(nodeId, nodeName);
        String expResult = "{\"id\":0,\"test_type\":{\"name\":\"Transaction\",\"id\":1},\"monitor\":{\"name\":\"Chrome\",\"id\":18},\"script\":\"open(\\\"www.google.com\\\")\\nopen(\\\"www.yahoo.com\\\")\",\"advanced_settings\":{\"additional_settings\":{\"capture_test_end_screenshot\":true,\"capture_http_headers\":true,\"capture_filmstrip\":true,\"ignore_ssl_failures\":false,\"treat_40X_or_50X_http_response_as_successful_test_run\":false}},\"nodes\":[{\"name\":\"Washington DC, US - Cogent\",\"id\":136}]}";
        String result = instance.createTransactionTestJson(transactionTestScript);
        assertEquals(expResult, result);
    }

    /**
     * Test of createDnsTestJson method, of class JsonBuilder.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateDnsTestJson() throws Exception {
        String DnsTestDomain = "www.catchpoint.com";
        JsonBuilder instance = new JsonBuilder(nodeId, nodeName);
        String expResult = "{\"id\":0,\"test_type\":{\"name\":\"DNS\",\"id\":5},\"monitor\":{\"name\":\"Experience\",\"id\":12},\"dns_query_type\":{\"name\":\"A\",\"id\":1},\"protocol\":{\"name\":\"Udp\",\"id\":0},\"test_domain\":\"www.catchpoint.com\",\"advanced_settings\":{\"additional_settings\":{\"disable_recursive_resolution\":false}},\"nodes\":[{\"name\":\"Washington DC, US - Cogent\",\"id\":136}]}";
        String result = instance.createDnsTestJson(DnsTestDomain);
        assertEquals(expResult, result);
    }

    /**
     * Test of createTracerouteTestJson method, of class JsonBuilder.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateTracerouteTestJson() throws Exception {
        String tracerouteTestTarget = "www.catchpoint.com";
        JsonBuilder instance = new JsonBuilder(nodeId, nodeName);
        String expResult = "{\"id\":0,\"test_type\":{\"name\":\"Trace Route\",\"id\":12},\"monitor\":{\"name\":\"Trace Route TCP\",\"id\":29},\"test_location\":\"www.catchpoint.com\",\"advanced_settings\":{\"additional_settings\":{\"disable_paris_traceroute_mode\":true}},\"nodes\":[{\"name\":\"Washington DC, US - Cogent\",\"id\":136}]}";
        String result = instance.createTracerouteTestJson(tracerouteTestTarget);
        assertEquals(expResult, result);
    }

}

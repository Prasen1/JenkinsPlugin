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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ResultBuilderTest {

    public ResultBuilderTest() {
        this.mapper = new ObjectMapper();
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

    ObjectMapper mapper;
    final String key = "DummyKey";
    final String secret = "DummySecret";
    final Integer nodeId = 0;
    final String nodeName = "DummyNode";
    final String testUrl = "https://www.example.com";
    final String testScript = "";
    final String testDomain = "";
    final String testTarget = "";

    /**
     * Test of mapItems method, of class ResultBuilder.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMapItems() throws Exception {
        String metricNames = "[{\"index\":1,\"id\":1,\"name\":\"Webpage Response (ms)\"},{\"index\":2,\"id\":2,\"name\":\"Document Complete (ms)\"},{\"index\":3,\"id\":3,\"name\":\"Render Start (ms)\"},{\"index\":4,\"id\":37,\"name\":\"Response (ms)\"},{\"index\":5,\"id\":5,\"name\":\"Dns (ms)\"},{\"index\":6,\"id\":6,\"name\":\"Connect (ms)\"},{\"index\":7,\"id\":7,\"name\":\"Wait (ms)\"},{\"index\":8,\"id\":8,\"name\":\"Load (ms)\"},{\"index\":9,\"id\":9,\"name\":\"#Failed Requests\"},{\"index\":10,\"id\":10,\"name\":\"#JS Failures\"},{\"index\":11,\"id\":11,\"name\":\"#Wire Requests\"},{\"index\":12,\"id\":74,\"name\":\"#Total Requests\"},{\"index\":13,\"id\":12,\"name\":\"#Connections\"},{\"index\":14,\"id\":13,\"name\":\"#Hosts\"},{\"index\":15,\"id\":14,\"name\":\"Summary Pagespeed Score\"},{\"index\":16,\"id\":15,\"name\":\"Downloaded Bytes\"},{\"index\":17,\"id\":16,\"name\":\"Request Cookie Bytes\"},{\"index\":18,\"id\":17,\"name\":\"Response Cookie Bytes\"},{\"index\":19,\"id\":77,\"name\":\"Speed Index\"},{\"index\":20,\"id\":87,\"name\":\"First Paint (ms)\"},{\"index\":21,\"id\":88,\"name\":\"First Contentful Paint (ms)\"},{\"index\":22,\"id\":89,\"name\":\"Visually Complete Time (ms)\"},{\"index\":23,\"id\":90,\"name\":\"Time to Interactive (ms)\"},{\"index\":24,\"id\":91,\"name\":\"Average Frames Per Second\"}]";
        String metricValues = "[89,null,null,89,0,0,63,7,0,0,1,1,1,1,null,42176,0,351,null,null,null,null,null,null]";
        JsonNode metricNamesNode = mapper.readTree(metricNames);
        JsonNode metricValuesNode = mapper.readTree(metricValues);
        ResultBuilder instance = new ResultBuilder(key, secret, nodeId, nodeName, testUrl, testScript, testDomain, testTarget);
        Map<String, String> expResult = new HashMap<String, String>() {
        };
        expResult.put("Dns (ms)", "0");
        expResult.put("#Wire Requests", "1");
        expResult.put("First Paint (ms)", "null");
        expResult.put("Visually Complete Time (ms)", "null");
        expResult.put("Response (ms)", "89");
        expResult.put("Wait (ms)", "63");
        expResult.put("#Total Requests", "1");
        expResult.put("#Connections", "1");
        expResult.put("#Hosts", "1");
        expResult.put("#Failed Requests", "0");
        expResult.put("Document Complete (ms)", "null");
        expResult.put("Webpage Response (ms)", "89");
        expResult.put("Response Cookie Bytes", "351");
        expResult.put("Time to Interactive (ms)", "null");
        expResult.put("Request Cookie Bytes", "0");
        expResult.put("Render Start (ms)", "null");
        expResult.put("Connect (ms)", "0");
        expResult.put("Speed Index", "null");
        expResult.put("#JS Failures", "0");
        expResult.put("First Contentful Paint (ms)", "null");
        expResult.put("Load (ms)", "7");
        expResult.put("Summary Pagespeed Score", "null");
        expResult.put("Average Frames Per Second", "null");
        expResult.put("Downloaded Bytes", "42176");
        Map result = instance.mapItems(metricNamesNode, metricValuesNode);
        assertEquals(expResult, result);
    }
}

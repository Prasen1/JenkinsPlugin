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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonBuilder {

    // Create common object nodes required by all test types
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootObjNode = mapper.createObjectNode();
    ObjectNode monitorNode = mapper.createObjectNode();
    ObjectNode advancedSettingNode = mapper.createObjectNode();
    ObjectNode additionalSettingsNode = mapper.createObjectNode();
    ObjectNode testNodesNode = mapper.createObjectNode();
    ArrayNode testNodeArrayNode = mapper.createArrayNode();

    // Required properties instantiated
    JsonBuilder(Integer nodeId, String nodeName) {
        testNodesNode.put("name", nodeName);
        testNodesNode.put("id", nodeId);
        testNodeArrayNode.add(testNodesNode);
    }

    // Creates and returns a json string for the chrome web test type
    public String createWebTestJson(String webTestUrl) throws JsonProcessingException {
        ObjectNode httpMethodNode = mapper.createObjectNode();
        httpMethodNode.put("name", "GET");
        httpMethodNode.put("id", 0);
        rootObjNode.set("http_method", httpMethodNode);
        rootObjNode.put("id", 0);
        ObjectNode testTypeNode = mapper.createObjectNode();
        testTypeNode.put("name", "Web");
        testTypeNode.put("id", 0);
        rootObjNode.set("test_type", testTypeNode);
        monitorNode.put("name", "Chrome");
        monitorNode.put("id", 18);
        rootObjNode.set("monitor", monitorNode);
        rootObjNode.put("test_url", webTestUrl);
        additionalSettingsNode.put("capture_test_end_screenshot", true);
        additionalSettingsNode.put("capture_http_headers", true);
        additionalSettingsNode.put("capture_filmstrip", true);
        additionalSettingsNode.put("ignore_ssl_failures", false);
        additionalSettingsNode.put("treat_40X_or_50X_http_response_as_successful_test_run", false);
        advancedSettingNode.set("additional_settings", additionalSettingsNode);
        rootObjNode.set("advanced_settings", advancedSettingNode);
        rootObjNode.set("nodes", testNodeArrayNode);
        String jsonString = mapper.writeValueAsString(rootObjNode);
        return jsonString;
    }

    // Creates and returns a json string for the chrome transaction test type
    public String createTransactionTestJson(String transactionTestScript) throws JsonProcessingException {
        rootObjNode.put("id", 0);
        ObjectNode testTypeNode = mapper.createObjectNode();
        testTypeNode.put("name", "Transaction");
        testTypeNode.put("id", 1);
        rootObjNode.set("test_type", testTypeNode);
        monitorNode.put("name", "Chrome");
        monitorNode.put("id", 18);
        rootObjNode.set("monitor", monitorNode);
        rootObjNode.put("script", transactionTestScript);
        additionalSettingsNode.put("capture_test_end_screenshot", true);
        additionalSettingsNode.put("capture_http_headers", true);
        additionalSettingsNode.put("capture_filmstrip", true);
        additionalSettingsNode.put("ignore_ssl_failures", false);
        additionalSettingsNode.put("treat_40X_or_50X_http_response_as_successful_test_run", false);
        advancedSettingNode.set("additional_settings", additionalSettingsNode);
        rootObjNode.set("advanced_settings", advancedSettingNode);
        rootObjNode.set("nodes", testNodeArrayNode);
        String jsonString = mapper.writeValueAsString(rootObjNode);
        return jsonString;
    }

    // Creates and returns a json string for the DNS Experience test type
    public String createDnsTestJson(String dnsTestDomain) throws JsonProcessingException {
        ObjectNode dnsQueryTypeNode = mapper.createObjectNode();
        ObjectNode protocolNode = mapper.createObjectNode();
        rootObjNode.put("id", 0);
        ObjectNode testTypeNode = mapper.createObjectNode();
        testTypeNode.put("name", "DNS");
        testTypeNode.put("id", 5);
        rootObjNode.set("test_type", testTypeNode);
        monitorNode.put("name", "Experience");
        monitorNode.put("id", 12);
        rootObjNode.set("monitor", monitorNode);
        dnsQueryTypeNode.put("name", "A");
        dnsQueryTypeNode.put("id", 1);
        rootObjNode.set("dns_query_type", dnsQueryTypeNode);
        protocolNode.put("name", "Udp");
        protocolNode.put("id", 0);
        rootObjNode.set("protocol", protocolNode);
        rootObjNode.put("test_domain", dnsTestDomain);
        additionalSettingsNode.put("disable_recursive_resolution", false);
        advancedSettingNode.set("additional_settings", additionalSettingsNode);
        rootObjNode.set("advanced_settings", advancedSettingNode);
        rootObjNode.set("nodes", testNodeArrayNode);
        String jsonString = mapper.writeValueAsString(rootObjNode);
        return jsonString;
    }

    // Creates and returns a json string for the TCP traceroute test type
    public String createTracerouteTestJson(String tracerouteTestTarget) throws JsonProcessingException {
        rootObjNode.put("id", 0);
        ObjectNode testTypeNode = mapper.createObjectNode();
        testTypeNode.put("name", "Trace Route");
        testTypeNode.put("id", 12);
        rootObjNode.set("test_type", testTypeNode);
        monitorNode.put("name", "Trace Route TCP");
        monitorNode.put("id", 29);
        rootObjNode.set("monitor", monitorNode);
        rootObjNode.put("test_location", tracerouteTestTarget);
        additionalSettingsNode.put("disable_paris_traceroute_mode", true);
        advancedSettingNode.set("additional_settings", additionalSettingsNode);
        rootObjNode.set("advanced_settings", advancedSettingNode);
        rootObjNode.set("nodes", testNodeArrayNode);
        String jsonString = mapper.writeValueAsString(rootObjNode);
        return jsonString;
    }

}

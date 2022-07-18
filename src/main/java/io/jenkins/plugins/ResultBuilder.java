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
import hudson.Launcher;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import javax.servlet.ServletException;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.kohsuke.stapler.DataBoundSetter;

public class ResultBuilder extends Builder implements SimpleBuildStep {

    private final String key;
    private final String secret;
    private final String testUrl;
    private final String testScript;
    private final String testDomain;
    private final String testTarget;
    private final Integer testNodeId;
    private final String testNodeName;
    private boolean failOnTestError;
    private boolean useInstantTest;
    private boolean useWebTest;
    private boolean useTransactionTest;
    private boolean useDnsTest;
    private boolean useTracerouteTest;

    @DataBoundConstructor
    public ResultBuilder(String key, String secret, Integer testNodeId, String testNodeName, String testUrl, String testScript, String testDomain, String testTarget) {
        this.key = key;
        this.secret = secret;
        this.testNodeId = testNodeId;
        this.testNodeName = testNodeName;
        this.testUrl = testUrl;
        this.testScript = testScript;
        this.testDomain = testDomain;
        this.testTarget = testTarget;
    }

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public String getTestScript() {
        return testScript;
    }

    public String getTestDomain() {
        return testDomain;
    }

    public String getTestTarget() {
        return testTarget;
    }

    public Integer getTestNodeId() {
        return testNodeId;
    }

    public String getTestNodeName() {
        return testNodeName;
    }

    public boolean isFailOnTestError() {
        return failOnTestError;
    }

    public boolean isUseInstantTest() {
        return useInstantTest;
    }

    public boolean isUseWebTest() {
        return useWebTest;
    }

    public boolean isUseTransactionTest() {
        return useTransactionTest;
    }

    public boolean isUseDnsTest() {
        return useDnsTest;
    }

    public boolean isUseTracerouteTest() {
        return useTracerouteTest;
    }

    @DataBoundSetter
    public void setUseWebTest(boolean useWebTest) {
        this.useWebTest = useWebTest;
    }

    @DataBoundSetter
    public void setUseTransactionTest(boolean useTransactionTest) {
        this.useTransactionTest = useTransactionTest;
    }

    @DataBoundSetter
    public void setUseDnsTest(boolean useDnsTest) {
        this.useDnsTest = useDnsTest;
    }

    @DataBoundSetter
    public void setUseTracerouteTest(boolean useTracerouteTest) {
        this.useTracerouteTest = useTracerouteTest;
    }

    @DataBoundSetter
    public void setFailOnTestError(boolean failOnTestError) {
        this.failOnTestError = failOnTestError;
    }

    @DataBoundSetter
    public void setUseInstantTest(boolean useInstantTest) {
        this.useInstantTest = useInstantTest;
    }

    // Map the metric names with metric values
    public Map mapItems(JsonNode metricNamesNode, JsonNode metricValuesNode) throws NullPointerException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String> metricNames = new ArrayList<>();
        ArrayList<String> metricValues = new ArrayList<>();
        for (final JsonNode metricNameNode : metricNamesNode) {
            String metricName = objectMapper.convertValue(metricNameNode.get("name").asText(), String.class);
            metricNames.add(metricName);
        }
        for (final JsonNode metricValueNode : metricValuesNode) {
            String metricValue = objectMapper.convertValue(metricValueNode.asText(), String.class);
            metricValues.add(metricValue);
        }
        Map<String, String> metrics = new HashMap<String, String>() {
        };
        for (int i = 0; i < metricValues.size(); i++) {
            metrics.put(metricNames.get(i), metricValues.get(i));
        }
        return metrics;
    }

    // Parses and prints the hopwise traceroute info
    // mapItems is not called because of a bug with the API that causes missing hop level metric values on hop timeouts
    // once the bug is addressed in v2 API, we can add mapItems call for printing hopwise metrics
    public void printTracerouteHopDetails(String metricLevelKey, JsonNode tracerouteItemsNode, TaskListener listener) {
        for (JsonNode itemNode : tracerouteItemsNode) {
            JsonNode hopsNode = itemNode.get(metricLevelKey);
            int count = 1;
            for (JsonNode hopNode : hopsNode) {
                listener.getLogger().println("-----------------------" + "[" + count + "]" + metricLevelKey.toUpperCase() + "--------------------------");
                String hopInfo = (hopNode.has("address")) ? " Address - " + hopNode.get("address").asText() : "";
                hopInfo += (hopNode.has("domain")) ? " Domain - " + hopNode.get("domain").asText() : "";
                hopInfo += (hopNode.has("status")) ? " Status - " + hopNode.get("status").asText() : "";
                listener.getLogger().println(hopInfo);
                count++;
            }
        }
    }

    // Parses and prints the levelwise DNS test info and metrics
    public void printDnsLevelDetails(ArrayList<String> fieldTypeKeys, JsonNode dnsFieldsNode, JsonNode dnsItemNode, TaskListener listener) {
        for (final String fieldTypeKey : fieldTypeKeys) {
            if (dnsItemNode.has(fieldTypeKey)) {
                JsonNode fieldsMetricsNode = dnsFieldsNode.get(fieldTypeKey).get("fields").get("metrics");
                JsonNode fieldTypeItemsNode = dnsItemNode.get(fieldTypeKey).get("items");
                listener.getLogger().println("-----------------------" + fieldTypeKey.toUpperCase() + "--------------------------");
                for (JsonNode fieldTypeItemNode : fieldTypeItemsNode) {
                    JsonNode itemsMetricsNode = fieldTypeItemNode.get("metrics");
                    Map<String, String> mappedMetrics = mapItems(fieldsMetricsNode, itemsMetricsNode);
                    String levelInfo = (fieldTypeItemNode.has("name")) ? " Name - " + fieldTypeItemNode.get("name").asText() : "";
                    levelInfo += (fieldTypeItemNode.has("dns_class")) ? " DNS Class - " + fieldTypeItemNode.get("dns_class").asText() : "";
                    levelInfo += (fieldTypeItemNode.has("dns_type")) ? " DNS Type - " + fieldTypeItemNode.get("dns_type").asText() : "";
                    levelInfo += (fieldTypeItemNode.has("info")) ? " Info - " + fieldTypeItemNode.get("info").asText() : "";
                    levelInfo += (fieldTypeItemNode.has("address")) ? " Address - " + fieldTypeItemNode.get("address").asText() : "";
                    levelInfo += (fieldTypeItemNode.has("domain")) ? " Domain - " + fieldTypeItemNode.get("domain").asText() : "";
                    listener.getLogger().println(levelInfo + " " + mappedMetrics);
                }
            }
        }
    }

    // Parses the root items array node and prints the test result to Jenkins build console log
    public void printTestResult(JsonNode rootItemsArrNode, TaskListener listener) throws CatchpointTestException {
        Boolean testFailed = false;
        String testTypeKey = "";
        ArrayList<String> metricLevelKeys = new ArrayList<>();
        ArrayList<String> fieldTypeKeys = new ArrayList<>();
        listener.getLogger().println("--------------------TEST INFORMATION--------------------");
        if (useWebTest) {
            listener.getLogger().println("Web Test URL: " + testUrl);
            testTypeKey = "web";
            metricLevelKeys.add("summary");
        }
        if (useTransactionTest) {
            testTypeKey = "transaction";
            metricLevelKeys.add("transaction_summary");
            metricLevelKeys.add("summary");
            metricLevelKeys.add("steps_summary");
        }
        if (useTracerouteTest) {
            listener.getLogger().println("Traceroute Test Target: " + testTarget);
            testTypeKey = "traceroute";
            metricLevelKeys.add("detail");
            metricLevelKeys.add("hops");
        }
        if (useDnsTest) {
            listener.getLogger().println("DNS Test Domain: " + testDomain);
            testTypeKey = "dns";
            metricLevelKeys.add("detail");
            metricLevelKeys.add("levels");
            fieldTypeKeys.add("query");
            fieldTypeKeys.add("answers");
            fieldTypeKeys.add("authoritative_nameservers");
            fieldTypeKeys.add("additional_records");
        }
        // Check root items array node has items which contain test data 
        if (rootItemsArrNode.size() > 0) {
            for (final JsonNode itemsArrNode : rootItemsArrNode) {
                try {
                    listener.getLogger().println("Test Run Time: " + itemsArrNode.get("runtime"));
                    listener.getLogger().println("Test Node: " + itemsArrNode.get("node").get("name"));
                    // If "test_failure" key is found then then the test has failed. Applicable for web and transaction test types
                    if (itemsArrNode.get(testTypeKey).has("test_failure")) {
                        testFailed = true;
                        listener.error("Instant Test Failed!");
                        listener.getLogger().println(itemsArrNode.get(testTypeKey).get("test_failure").get("id") + " : " + itemsArrNode.get(testTypeKey).get("test_failure").get("description"));
                    }
                    JsonNode fieldsMetricsNode;
                    JsonNode itemsNode;
                    for (final String metricLevelKey : metricLevelKeys) {
                        if ("traceroute".equals(testTypeKey) && "hops".equals(metricLevelKey)) {
                            fieldsMetricsNode = itemsArrNode.get(testTypeKey).get("detail").get("fields").get(metricLevelKey).get("fields").get("metrics");
                            itemsNode = itemsArrNode.get(testTypeKey).get("detail").get("items");
                            printTracerouteHopDetails(metricLevelKey, itemsNode, listener);
                        } else if ("dns".equals(testTypeKey) && "levels".equals(metricLevelKey)) {
                            itemsNode = itemsArrNode.get(testTypeKey).get(metricLevelKey).get("items");
                            int count = 1;
                            for (final JsonNode itemNode : itemsNode) {
                                listener.getLogger().println("-----------------------" + "[" + count + "]" + metricLevelKey.toUpperCase() + "--------------------------");
                                listener.getLogger().println(itemNode.get("fields"));
                                JsonNode fieldsNode = itemsArrNode.get(testTypeKey).get(metricLevelKey).get("fields");
                                printDnsLevelDetails(fieldTypeKeys, fieldsNode, itemNode, listener);
                                count++;
                            }
                        } else {
                            fieldsMetricsNode = itemsArrNode.get(testTypeKey).get(metricLevelKey).get("fields").get("metrics");
                            itemsNode = itemsArrNode.get(testTypeKey).get(metricLevelKey).get("items");
                            for (int item = 0; item < itemsNode.size(); item++) {
                                int count = item + 1;
                                // If "error" key is found and value does not equal "None" then then the test has failed. Applicable for traceroute and DNS test types
                                if (itemsNode.get(item).has("error")) {
                                    if (!itemsNode.get(item).get("error").asText().equalsIgnoreCase("None")) {
                                        testFailed = true;
                                        listener.error("Instant Test Failed!");
                                        listener.getLogger().println(itemsNode.get(item).get("error").asText());
                                    }
                                }
                                JsonNode itemsMetricsNode = itemsNode.get(item).get("metrics");
                                Map<String, String> mappedMetrics = mapItems(fieldsMetricsNode, itemsMetricsNode);
                                String itemInfo = (itemsNode.get(item).has("test_url")) ? " Test URL - " + itemsNode.get(item).get("test_url").asText() : "";
                                itemInfo += (itemsNode.get(item).has("address")) ? itemsNode.get(item).get("address").asText() : "";
                                itemInfo += (itemsNode.get(item).has("domain")) ? " Domain - " + itemsNode.get(item).get("domain").asText() : "";
                                itemInfo += (itemsNode.get(item).has("status")) ? " Status - " + itemsNode.get(item).get("status").asText() : "";
                                listener.getLogger().println("-----------------------" + "[" + count + "]" + metricLevelKey.toUpperCase() + "--------------------------");
                                listener.getLogger().println(itemInfo + " " + mappedMetrics);
                            }
                        }
                    }
                } catch (NullPointerException npe) {
                    listener.error("One or more missing items! Check the plugin configuration and re-run the build step. If this error persists report it to Catchpoint Support");
                    throw npe;
                }
            }
            // Throw CatchpointTestException and cause the Jenkins build to fail if failOnTestError option is enabled and the test has failed
            if (failOnTestError && testFailed) {
                throw new CatchpointTestException("Catchpoint instant test failed with an error");
            }
        } else {
            throw new CatchpointTestException("No data, Test may be still running");
        }
    }

    // Overridden method that performs the actual Jenkins build step
    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        String requestBody = "";
        final Integer SLEEP_TIME = 10000;
        final Integer MAX_RETRY_COUNT = 6;
        ObjectMapper objectMapper = new ObjectMapper();

        listener.getLogger().println("Starting API calls");
        String getTokenResult[] = RequestMaker.getToken(key, secret);
        listener.getLogger().println("POST Credentials: " + getTokenResult[0]);
        Map tokenObject = objectMapper.readValue(getTokenResult[1], Map.class
        );
        String token = tokenObject.get("access_token").toString();
        token = Base64.getEncoder().encodeToString(token.getBytes("UTF-8"));

        JsonBuilder buildJson = new JsonBuilder(testNodeId, testNodeName);
        if (useWebTest && testUrl.length() > 0) {
            requestBody = buildJson.createWebTestJson(testUrl);
        } else if (useTransactionTest && testScript.length() > 0) {
            requestBody = buildJson.createTransactionTestJson(testScript);
        } else if (useTracerouteTest && testTarget.length() > 0) {
            requestBody = buildJson.createTracerouteTestJson(testTarget);
        } else if (useDnsTest && testDomain.length() > 0) {
            requestBody = buildJson.createDnsTestJson(testDomain);
        } else {
            throw new IllegalArgumentException("Something went wrong, please check the plugin configuration");
        }

        String postTestResult[] = RequestMaker.postTest(token, requestBody, useInstantTest);
        listener.getLogger().println("POST Execute Test: " + postTestResult[0]);
        Map testIdObject = objectMapper.readValue(postTestResult[1], Map.class
        );
        listener.getLogger().println("Got Response: " + testIdObject);
        String testId = testIdObject.get("id").toString();

        String getTestResult[] = RequestMaker.getTestResult(token, testId, useInstantTest);
        listener.getLogger().println("GET Test Result: " + getTestResult[0]);
        JsonNode itemsArrNode = objectMapper.readTree(getTestResult[1]).get("items");

        if (itemsArrNode.isArray()) {
            // If root items array node has 0 items then test data is not present 
            // Poll the GET onDemand/Instant test result api upto 6 times with a sleep time of 10 seconds in between
            for (int retryCount = 1; retryCount <= MAX_RETRY_COUNT; retryCount++) {
                if (itemsArrNode.size() == 0) {
                    listener.getLogger().println("No data, test may be still running. Retrying GET Test Result: " + retryCount);
                    Thread.sleep(SLEEP_TIME);
                    String getRetryTestResult[] = RequestMaker.getTestResult(token, testId, useInstantTest);
                    itemsArrNode = objectMapper.readTree(getRetryTestResult[1]).get("items");
                } else {
                    listener.getLogger().println("Test data retrieved");
                    break;
                }
            }
            printTestResult(itemsArrNode, listener);
        }
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public boolean isMoreThanOneTestSelected(boolean webtest, boolean transactionTest, boolean dnsTest, boolean tracerouteTest) {
            return ((webtest ? 1 : 0) + (transactionTest ? 1 : 0) + (dnsTest ? 1 : 0) + (tracerouteTest ? 1 : 0) > 1);
        }

        public FormValidation doCheckKey(@QueryParameter("key") final String apiKey, @QueryParameter("secret") final String apiSecret)
                throws IOException, ServletException {
            if (apiKey.length() == 0) {
                return FormValidation.error("No API Key provided");
            }
            if (apiSecret.length() == 0) {
                return FormValidation.error("No API Secret provided");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckUseWebTest(@QueryParameter("useWebTest") boolean useCatchpointWebTest, @QueryParameter("useTransactionTest") boolean useCatchpointTransactionTest, @QueryParameter("useTracerouteTest") boolean useCatchpointTracerouteTest, @QueryParameter("useDnsTest") boolean useCatchpointDnsTest, @QueryParameter("testUrl") final String catchpointTestUrl)
                throws IOException, ServletException {
            if (isMoreThanOneTestSelected(useCatchpointWebTest, useCatchpointTransactionTest, useCatchpointDnsTest, useCatchpointTracerouteTest)) {
                return FormValidation.error("Only one Test type can be selected");
            }
            if (useCatchpointWebTest && catchpointTestUrl != null) {
                Boolean properUrlFormat = catchpointTestUrl.matches("(?i)(^https?://[^\\s]+$)|(.*?\\$\\{.+(\\(.*\\))?\\}.*)");
                if (catchpointTestUrl.length() == 0 || properUrlFormat == false) {
                    return FormValidation.error("Invalid Test URL provided");
                }
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckUseTransactionTest(@QueryParameter("useTransactionTest") boolean useCatchpointTransactionTest, @QueryParameter("useWebTest") boolean useCatchpointWebTest, @QueryParameter("useTracerouteTest") boolean useCatchpointTracerouteTest, @QueryParameter("useDnsTest") boolean useCatchpointDnsTest, @QueryParameter("testScript") final String catchpointTestScript)
                throws IOException, ServletException {
            if (isMoreThanOneTestSelected(useCatchpointWebTest, useCatchpointTransactionTest, useCatchpointDnsTest, useCatchpointTracerouteTest)) {
                return FormValidation.error("Only one Test type can be selected");
            }
            if (useCatchpointTransactionTest && catchpointTestScript != null) {
                if (catchpointTestScript.length() == 0) {
                    return FormValidation.error("Test script was not provided");
                }
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckUseDnsTest(@QueryParameter("useDnsTest") boolean useCatchpointDnsTest, @QueryParameter("useTracerouteTest") boolean useCatchpointTracerouteTest, @QueryParameter("useTransactionTest") boolean useCatchpointTransactionTest, @QueryParameter("useWebTest") boolean useCatchpointWebTest, @QueryParameter("testDomain") final String catchpointTestDomain)
                throws IOException, ServletException {
            if (isMoreThanOneTestSelected(useCatchpointWebTest, useCatchpointTransactionTest, useCatchpointDnsTest, useCatchpointTracerouteTest)) {
                return FormValidation.error("Only one Test type can be selected");
            }
            if (useCatchpointDnsTest && catchpointTestDomain != null) {
                Boolean properTestDomainFormat = catchpointTestDomain.matches("(?i)(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z0-9][a-z0-9-]{0,61}[a-z0-9]");
                if (catchpointTestDomain.length() == 0 || properTestDomainFormat == false) {
                    return FormValidation.error("Invalid Test domain provided");
                }
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckUseTracerouteTest(@QueryParameter("useTracerouteTest") boolean useCatchpointTracerouteTest, @QueryParameter("useDnsTest") boolean useCatchpointDnsTest, @QueryParameter("useTransactionTest") boolean useCatchpointTransactionTest, @QueryParameter("useWebTest") boolean useCatchpointWebTest, @QueryParameter("testTarget") final String catchpointTestTarget)
                throws IOException, ServletException {
            if (isMoreThanOneTestSelected(useCatchpointWebTest, useCatchpointTransactionTest, useCatchpointDnsTest, useCatchpointTracerouteTest)) {
                return FormValidation.error("Only one Test type can be selected");
            }
            if (useCatchpointTracerouteTest && catchpointTestTarget != null) {
                Boolean properTestTargetFormat = catchpointTestTarget.matches("(?i)^([^\\s:.,]+(\\.[^\\s:.,]+)+)((.+):\\d+)?$");
                if (catchpointTestTarget.length() == 0 || properTestTargetFormat == false) {
                    return FormValidation.error("Invalid Traceroute Test location provided");
                }
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckTestNodeId(@QueryParameter("testNodeId") final String catchpointTestNodeId, @QueryParameter("testNodeName") final String catchpointTestNodeName)
                throws IOException, ServletException {
            if (catchpointTestNodeId.length() == 0 || catchpointTestNodeName.length() == 0) {
                return FormValidation.error("Node Id or Name was not provided");
            }
            if (catchpointTestNodeId.length() != 0) {
                try {
                    Integer.parseInt(catchpointTestNodeId);
                } catch (NumberFormatException e) {
                    return FormValidation.error("Node Id should be a number");
                }
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Catchpoint Instant Test";
        }
    }
}

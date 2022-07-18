# Catchpoint Instant Test Plugin

## Introduction

A Jenkins plugin for running Catchpoint onDemand/Instant Tests. Test types supported are: Web - Chrome Monitor, Transaction - Chrome Monitor, Traceroute - TCP Monitor, DNS -  Experience Monitor.

## Getting started

### Installation

To install the plugin, perform the following steps:

1. Download the latest plugin. It will be a .hpi file under Releases
2. Login to your Jenkins server. Note that administrator permission is required to install a plugin.
3. Navigate to /jenkins/pluginManager/advanced to go to the Advanced Plugin Manager page.
4. Scroll and find the section labeled "Upload Plugin"
5. Click on the "Choose File" button
6. Find the downloaded .hpi file and select it
7. Click on the "Upload" button
8. Restart Jenkins to complete the plugin installation.

### Configuration

The installed plugin will be available for selection as a build step in Freestyle projects. To use the plugin, perform the following steps:

 1. Navigate to /configure page of your Freestyle project
 2. Click on the button labeled "Add build step"
 3. Select "Catchpoint Instant Test"
 4. Enter your Catchpoint v1 API Key and Secret on the respective plugin form inputs
 5. Select any one of the Test types 
 6. Enter the value in the input box according to the Test type selected. Note that only one Test type can be selected per plugin setup
 7. Enter the Catchpoint Node Id and Name to run the Test from
 8. Optionally, enable "Fail on Error" to mark the build failed if the onDemand/Instant Test fails with a test error
 9. "Use Instant Test" is also an optional setting to make use of the InstantTest endpoint  
 10. Save the project to start using this build step.
 
 
 ![Plugin Interface](/PluginInterface.PNG?raw=true "Plugin Form")
 Fig 1: Plugin Form
 
## Result

The Test result will be printed in console output and can be found under "Build History" of the project. The console output will contain informational logs such as the API call response codes and status, the Instant Test Id, the number of retries made to fetch the test result, and below that, the Test result.


![Web Test Result](/ResultWebTest.PNG?raw=true "Successful Web Test result")
Fig 2: Console output of a successful Instant Web Test


![DNS Test Result](/ResultDnsTestFailed.PNG?raw=true "Failed DNS Test result")
Fig 3: Console output of a failed Instant DNS Experience Test with "Fail on Error" plugin setting enabled

**Note:** The number of API calls consumed will depend on how long the Test runs. If the Test result is retrieved on first try then total number of API calls consumed will be 3 - 1 for fetching the token, 1 for running the Test and 1 for retrieving the Test result. If the Test result is not retrieved on first try then the plugin will poll the GET OnDemand/Instant Test Result API 6 more times. The number of retries can be seen in the console output.

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)


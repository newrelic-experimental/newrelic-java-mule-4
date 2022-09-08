[![New Relic Experimental header](https://github.com/newrelic/opensource-website/raw/master/src/images/categories/Experimental.png)](https://opensource.newrelic.com/oss-category/#new-relic-experimental)

![GitHub forks](https://img.shields.io/github/forks/newrelic-experimental/newrelic-java-mule-4?style=social)
![GitHub stars](https://img.shields.io/github/stars/newrelic-experimental/newrelic-java-mule-4?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/newrelic-experimental/newrelic-java-mule-4?style=social)

![GitHub all releases](https://img.shields.io/github/downloads/newrelic-experimental/newrelic-java-mule-4/total)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/newrelic-experimental/newrelic-java-mule-4)
![GitHub last commit](https://img.shields.io/github/last-commit/newrelic-experimental/newrelic-java-mule-4)
![GitHub Release Date](https://img.shields.io/github/release-date/newrelic-experimental/newrelic-java-mule-4)


![GitHub issues](https://img.shields.io/github/issues/newrelic-experimental/newrelic-java-mule-4)
![GitHub issues closed](https://img.shields.io/github/issues-closed/newrelic-experimental/newrelic-java-mule-4)
![GitHub pull requests](https://img.shields.io/github/issues-pr/newrelic-experimental/newrelic-java-mule-4)
![GitHub pull requests closed](https://img.shields.io/github/issues-pr-closed/newrelic-experimental/newrelic-java-mule-4)

# New Relic Java Instrumentation for Mule 4.x

Instrumentation for the Mule 4.x framework.  

## Accouncement
The original releases of the Mule 4.x instrumentation used the New Relic Java Agent Asynchronous framework to tie the transaction together.  But in some Mule applications it was resulting in some of the asynchronous tokens not being expired and some async segments not being ended.  Because these items are stored in static caches it could consume large amounts of memory and cause problems.  In Version 3.0 we started using distributed tracing headers to tie things together.  This results in more transactions being reported but everything gets tied together in the Distributed Tracing view and it does not cause memory issues.   If the desire is to have one conhesive transaction then use Release V2.0 provided that memory is not being consumed.  If the distributed tracing view is acceptable then use Release V3.0

## Installation

To install:

1. Download the latest release jar files.   
2. In the New Relic Java directory (the one containing newrelic.jar), create a directory named extensions if it does not already exist.
3. Copy the downloaded jars into the extensions directory.
4. Restart the application.

## Getting Started

Once installed, the instrumentation will track transactions through the various Mule components.

## Building

Note that the jar necessary for building the Mule-Agent extension are no longer available in Maven.  As a result, the extension has been removed from the build process.  It can be built if you have access to either AnypointStudio or a Mule Server.  Follow the instructions here to build: https://github.com/newrelic-experimental/newrelic-java-mule-4/blob/main/Mule-Agent-Build.md


Building the extension requires that Gradle is installed.
To build the extension jars from source, follow these steps:
### Build single extension
To build a single extension with name *extension*, do the following:
1. Set an environment variable *NEW_RELIC_EXTENSIONS_DIR* and set its value to the directory where you want the jar file built.
2. Run the command: ./gradlew *extension*:clean *extension*:install
### Build all extensions
To build all extensions, do the following:
1. Set an environment variable *NEW_RELIC_EXTENSIONS_DIR* and set its value to the directory where you want the jar file built.
2. Run the command: ./gradlew clean install

## Support

New Relic has open-sourced this project. This project is provided AS-IS WITHOUT WARRANTY OR DEDICATED SUPPORT. Issues and contributions should be reported to the project here on GitHub.

>We encourage you to bring your experiences and questions to the [Explorers Hub](https://discuss.newrelic.com) where our community members collaborate on solutions and new ideas.

## Contributing

We encourage your contributions to improve [Project Name]! Keep in mind when you submit your pull request, you'll need to sign the CLA via the click-through using CLA-Assistant. You only have to sign the CLA one time per project. If you have any questions, or to execute our corporate CLA, required if your contribution is on behalf of a company, please drop us an email at opensource@newrelic.com.

**A note about vulnerabilities**

As noted in our [security policy](../../security/policy), New Relic is committed to the privacy and security of our customers and their data. We believe that providing coordinated disclosure by security researchers and engaging with the security community are important means to achieve our security goals.

If you believe you have found a security vulnerability in this project or any of New Relic's products or websites, we welcome and greatly appreciate you reporting it to New Relic through [HackerOne](https://hackerone.com/newrelic).

## License

[Project Name] is licensed under the [Apache 2.0](http://apache.org/licenses/LICENSE-2.0.txt) License.

>[If applicable: [Project Name] also uses source code from third-party libraries. You can find full details on which libraries are used and the terms under which they are licensed in the third-party notices document.]

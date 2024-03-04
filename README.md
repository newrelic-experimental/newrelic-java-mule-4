<a href="https://opensource.newrelic.com/oss-category/#new-relic-experimental"><picture><source media="(prefers-color-scheme: dark)" srcset="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/dark/Experimental.png"><source media="(prefers-color-scheme: light)" srcset="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/Experimental.png"><img alt="New Relic Open Source experimental project banner." src="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/Experimental.png"></picture></a>

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
## Notes on extension version numbers
Many of the instrumentation modules include a version number appended to the end of the name.  For example, Mule-Core-4.3.  Those with no version numbers represent the initial releases (typically 4.0) and when it no longer works in a newer version, a new extension is created with the same name and -4.xxx where 4.xxx is the version where the instrumentation stopped working.   So even if you are using a newer version, the latest version should still work.  We do regularly run a verify process to ensure our instrumentation works against all current versions but if you find it is not working in a particular version, please open an issue and we will attempt to address it.   
The following table shows the versions covered by each extension:    
   
|Extension | Versions |  
| --- | --- |   
| Mule-APIKit-Module | 1.0 - 1.1.9  |  
| Mule-APIKit-Module-1.1.10 | 1.1.10 - 1.3.3 |
| Mule-APIKit-Module-1.3.4 | 1.3.3 & later |
| Mule-Batch-ee | 4.2.2 - 4.4.x |
| Mule-Batch-ee-4.5 | 4.5.0 and later |
| Mule-Core-4 | 4.0.x |
| Mule-Core-4.1.0 | 4.1.0 - 4.1.2 |
| Mule-Core-4.1.3 | 4.1.3 - 4.1.6 |
| Mule-Core-4.2 | 4.2.0 - 4.2.1 |
| Mule-Core-4.2.2 | 4.2.2 |
| Mule-Core-4.3 | 4.3.0 |
| Mule-Core-4.4 | 4.4.0 |
| Mule-Core-4.4-2022x | all 4.4.0-2022 versions |
| Mule-Core-4.5 | 4.5.1 and later |
| Mule-Core-4.5-2022x | all 4.5.0-2022 versions |
| Mule-Extensions | 4.0.0 - 4.2.2 |
| Mule-Extensions-4.3 | 4.3.0 |
| Mule-Extensions-4.4 | 4.4.0 including 2022 versions |


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

Note that the a jar necessary for building the Mule-Batch-ee extension are no longer available in Maven.  As a result, the extension has been removed from the build process.  It can be built if you have access to either AnypointStudio or a Mule Server.  Follow the instructions here to build: https://github.com/newrelic-experimental/newrelic-java-mule-4/blob/main/Mule-Batch-Build.md
 

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

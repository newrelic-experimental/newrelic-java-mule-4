## Version: [v3.1.3](https://github.com/newrelic-experimental/newrelic-java-mule-4/releases/tag/v3.1.3) | Created: 2024-01-31
### Bug Fixes
- Fixed instrumentation for mule core 4.1.x
- Fixed instrumentation for mule extensions


## Version: [v3.1.2](https://github.com/newrelic-experimental/newrelic-java-mule-4/releases/tag/v3.1.2) | Created: 2023-11-29
### Bug Fixes
- Fixes to Mule-Core-5.x
- Merge pull request #18 from newrelic-experimental/fix-mule-core-4.5
- Upgraded plugin de.undercouch.download version 5.0.0
- Fixed excludes so verify passes
- Merge pull request #19 from newrelic-experimental/fix-mule-core-4.5


## Version: [v3.1.1](https://github.com/newrelic-experimental/newrelic-java-mule-4/releases/tag/v3.1.1) | Created: 2023-11-01
### Features
- Added support for mule 4.5 and above


## Version: [v3.0.1](https://github.com/newrelic-experimental/newrelic-java-mule-4/releases/tag/v3.0.1) | Created: 2023-09-27
### Bug Fixes
- Fixed problems with events and added http connector
- Fixed to http problems
- Fixed null pointer in 4.2.2 and fixed problem with acceptDistributedTracePayload
- Fixed to take care of build that was broken when Maven repo was removed
- Merge pull request #12 from newrelic-experimental/muleagentfix
- Fixed to build due to removal of jar from Maven
- Merge branch 'main' into mulebatchfix
- Merge pull request #14 from newrelic-experimental/mulebatchfix

### Build Upgrades
- Updated vendor title and added release workflow


## Installation

To install:

1. Download the latest release jar files.
2. In the New Relic Java directory (the one containing newrelic.jar), create a directory named extensions if it does not already exist.
3. Copy the downloaded jars into the extensions directory.
4. Restart the application.   


<a href="https://opensource.newrelic.com/oss-category/#new-relic-experimental"><picture><source media="(prefers-color-scheme: dark)" srcset="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/dark/Experimental.png"><source media="(prefers-color-scheme: light)" srcset="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/Experimental.png"><img alt="New Relic Open Source experimental project banner." src="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/Experimental.png"></picture></a>

# New Relic Java Instrumentation for Mule 4.9.x works with the Latest New Relic Java Agent Released After May 2026.

## Overview

This instrumentation provides full observability for MuleSoft 4.9.x (LTS) applications running on the New Relic Java Agent. Mule 4.9.x uses a fully reactive model (Project Reactor) which requires specialized async token linking across thread boundaries.

### What's Monitored
- Flow transactions with proper naming (`OtherTransaction/Flows/{flowName}`)
- Individual processor spans (Set Payload, Logger, Transform, etc.)
- HTTP listener (inbound requests)
- HTTP requester (outbound external calls) with External segment
- Distributed tracing headers (inbound and outbound)
- Error reporting
- Application-level naming per Mule app

---

## Required Extension Jars

Deploy the following jars to `{MULE_HOME}/newrelic/extensions/`:

### Core Modules (required)


To install:

1. Download the latest release jar files.   
2. In the New Relic Java directory (the one containing newrelic.jar), create a directory named extensions if it does not already exist.
3. Copy the downloaded jars into the extensions directory.
4. Restart the application.

### Executor Module (required for async thread linking)
| Jar | Purpose |
|-----|---------|
| `executors-17.jar` | ForkJoinPool/ThreadPool token propagation (JDK 17 API) |
| `executors-22.jar` | ForkJoinPool/ThreadPool token propagation (JDK 22 API) |

**Important:** Deploy ONLY `executors-17` and `executors-22`. Do NOT deploy other executor versions (8, 9, 10, 21) — they create competing tokens that cause transaction timeouts.

---

## newrelic.yml Configuration

Add the following to your `newrelic.yml` under the `common:` section:

```yaml
common: &default_settings

  # ... existing settings ...

  # ============================================================
  # Mule 4.9.x Required Settings
  # ============================================================

  # Disable built-in modules that conflict with Mule extensions
  class_transformer:

    # Disable built-in ning HTTP client — replaced by custom Mule-Ning-Http module
    com.newrelic.instrumentation.ning-async-http-client-1.0:
      enabled: false
    com.newrelic.instrumentation.ning-async-http-client-1.1:
      enabled: false
    com.newrelic.instrumentation.ning-async-http-client-1.6.1:
      enabled: false

    # Disable built-in CompletableFuture modules — conflict with executor module
    com.newrelic.instrumentation.java.completable-future-jdk8u40:
      enabled: false
    com.newrelic.instrumentation.java.completable-future-jdk11:
      enabled: false
```

### Why These Settings Are Required

**ning-async-http-client disabled:** Mule uses a customized version of the ning HTTP client (Grizzly). The built-in NR module can't properly close HTTP segments because Mule wraps the async handler chain. Our custom `Mule-Ning-Http` module handles this with a `transferToHandler()` pattern that bridges the wrapper chain.

**completable-future disabled:** Mule 4.9.x's reactive model creates many CompletableFuture instances during pipeline setup. The built-in modules create `Token/Internal` tokens for each one — these tokens leak because Mule's completions fire on Grizzly I/O threads outside the tracked lifecycle. The executor module (`executors-17/22`) handles the thread linking we need.

---

## Building

### Prerequisites
- Java 17+
- Gradle
- New Relic Java Agent jars in `libs/` directory

### Build All Modules
```bash
export NEW_RELIC_EXTENSIONS_DIR=/path/to/extensions
./gradlew clean install
```

### Build Individual Module
```bash
./gradlew :Mule-Core-4.9.4:clean :Mule-Core-4.9.4:jar
./gradlew :Mule-Extensions-4.3:clean :Mule-Extensions-4.3:jar
./gradlew :Mule-Ning-Http:clean :Mule-Ning-Http:jar
```

### Build Executor Modules (separate repo)
```bash
cd /path/to/newrelic-java-executors
./gradlew :executors-17:clean :executors-17:jar
./gradlew :executors-22:clean :executors-22:jar
```

---



## Architecture: Async Thread Linking

Mule 4.9.x processes requests across multiple threads. Three async bridges link them:

```
┌─────────────────┐     Bridge 1      ┌──────────────────┐     Bridge 2      ┌─────────────────┐
│   Flow Thread    │ ──────────────→   │  ForkJoinPool    │ ──────────────→   │  Grizzly I/O    │
│                  │  (executor module │    Worker         │  (ExecutorCallback│    Thread        │
│ FlowProcessMedia │   NRCallable)     │                  │   nrToken)        │                  │
│ tor.process()    │                   │ CompletableComp  │                   │ ExecutorCompletion│
│                  │                   │ onentExecutor    │                   │ CallbackAdapter  │
│ routeEventAsync()│                   │ .execute()       │                   │ .success()       │
└─────────────────┘                   └──────────────────┘                   └─────────────────┘
                                              │
                                              │ Bridge 3 (custom ning module)
                                              │ NingTokenCache.transferToHandler()
                                              ▼
                                      ┌──────────────────┐
                                      │ Grizzly I/O      │
                                      │ (HTTP response)   │
                                      │                   │
                                      │ ResponseAsync     │
                                      │ Handler           │
                                      │ .onCompleted()    │
                                      │ → segment.end()   │
                                      └──────────────────┘
```



---

## Support

New Relic has open-sourced this project. This project is provided AS-IS WITHOUT WARRANTY OR DEDICATED SUPPORT. Issues and contributions should be reported to the project here on GitHub.

**A note about vulnerabilities**

As noted in our [security policy](../../security/policy), New Relic is committed to the privacy and security of our customers and their data. We believe that providing coordinated disclosure by security researchers and engaging with the security community are important means to achieve our security goals.

If you believe you have found a security vulnerability in this project or any of New Relic's products or websites, we welcome and greatly appreciate you reporting it to New Relic through [HackerOne](https://hackerone.com/newrelic).

## License

Licensed under the [Apache 2.0](http://apache.org/licenses/LICENSE-2.0.txt) License.

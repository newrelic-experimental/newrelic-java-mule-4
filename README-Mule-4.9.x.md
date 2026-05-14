# New Relic Java Instrumentation for Mule 4.9.x

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
| Jar | Purpose |
|-----|---------|
| `Mule-Core-4.9.4.jar` | Core flow instrumentation — processors, event context, flow mediator |
| `Mule-Extensions-4.3.jar` | Extension operation executor — token linking for async completion chain |
| `Mule-Ning-Http.jar` | Custom HTTP client instrumentation — closes external call segments properly |
| `Mule-HttpConnector.jar` | HTTP listener/requester tracer factory |

### Supporting Modules (required)
| Jar | Purpose |
|-----|---------|
| `Mule-API.jar` | ExecutableComponent instrumentation |
| `Mule-Extensions-4.4.jar` | CompletionCallback, DefaultSourceCallback |
| `Mule-Extensions-4.5.jar` | Enhanced source callback with application naming |
| `Mule-Http.jar` | Grizzly HTTP listener (inbound) |
| `Mule-Http-1.8.24.jar` | HTTP listener for connector version 1.8.24 |
| `Mule-Http-Api.jar` | HTTP API route matching |
| `Mule-Http-Api-4.2.jar` | HTTP API 4.2 |
| `Mule-Scheduler.jar` | Scheduler instrumentation |

### Executor Module (required for async thread linking)
| Jar | Purpose |
|-----|---------|
| `executors-17.jar` | ForkJoinPool/ThreadPool token propagation (JDK 17 API) |
| `executors-22.jar` | ForkJoinPool/ThreadPool token propagation (JDK 22 API) |

**Important:** Deploy ONLY `executors-17` and `executors-22`. Do NOT deploy other executor versions (8, 9, 10, 21) — they create competing tokens that cause transaction timeouts.

### Optional Modules
| Jar | Purpose |
|-----|---------|
| `Mule-Core-4.7.jar` through `Mule-Core-4.9.2.jar` | Support for older Mule 4.x versions |
| `Mule-Http-1.2.jar` | Older HTTP connector version |

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

## Verification

After deployment and restart, verify instrumentation is active:

```bash
LOG=/path/to/newrelic/logs/nrmule.log

# 1. Module validation (should show "validated classloader")
grep "Mule-Core-4.9.4.*validated" $LOG
grep "Mule-Extensions-4.3.*validated" $LOG
grep "Mule-Ning-Http.*validated" $LOG

# 2. Classes weaved (should be ~84 for Mule-Core)
grep -c "Mule-Core-4.9.4.*weaved" $LOG

# 3. No field errors (should be 0)
grep -c "Could not find required field name" $LOG

# 4. Built-in modules disabled
grep "ning.*disabled\|completable-future.*disabled" $LOG

# 5. Transaction finishing with reasonable duration
grep "OtherTransaction/Flows.*finished" $LOG | tail -5
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

## Known Limitations

### Disabled Instrumentation Points (Mule 4.9.x reactive model)
- **`AbstractProcessingStrategy`** consumer wrapping — deadlocks reactive pipeline
- **`AbstractEventContext` constructors** — deadlocks during reactive initialization
- **`Sink.emit()/accept()`** — token leaks from multiple Sink instances
- **`retransformUninstrumentedClass()`** in NREventConsumer — blocks reactor thread

### DT UI "Missing Parent" Spans
`ResponseAsyncHandler/onCompleted` and the external call span may show "missing parent" in the Distributed Tracing UI. This is cosmetic — the Transaction Trace UI shows them correctly linked. The cause is `@Trace(async=true)` creating a span on the Grizzly I/O thread where no parent span context exists at tracer creation time.

### JPMS Constraints
- `Processor.process()` cannot call utility classes in `com.newrelic.mule.core` for processors in `org.mule.runtime.core.components` module — simplified to only set metric name
- Utility class fields accessed from weaved code must be NON-final (causes `IllegalAccessError` across JPMS boundaries)

---

## Troubleshooting

See [TROUBLESHOOTING-GUIDE.md](TROUBLESHOOTING-GUIDE.md) for detailed diagnosis steps covering:
- Module loading failures
- App hanging
- Token leaks
- Segment timeouts
- Handler wrapper chain issues

---

## Support

New Relic has open-sourced this project. This project is provided AS-IS WITHOUT WARRANTY OR DEDICATED SUPPORT. Issues and contributions should be reported to the project here on GitHub.

## License

Licensed under the [Apache 2.0](http://apache.org/licenses/LICENSE-2.0.txt) License.

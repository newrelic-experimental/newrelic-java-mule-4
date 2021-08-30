# Span Attributes
When using New Relic Java Agent 6.4 or later and release 2.0 or later of the Mule 4 instrumentation, attributes are added to the spans in Distributed Tracing.  This document details the attributes reported.   
## Classes implementing the Processor interface and the process method
### Core Event
The process method has an input CoreEvent parameter and returns an output CoreEvent.  The key name is prefixed with the direction and a '-'   
   
| Key |
| --- |
| Input-CorrelationId | 
| Input-ID | 
| Input-Location | 
| Returned-CorrelationId | 
| Returned-ID | 
| Returned-Location | 
   
## Classes implementing the Sink interface and the emit method
### Core Event
The emit method has an input CoreEvent parameter.  The key name is prefixed with the direction and a '-'   
   
| Key |
| --- |
| Input-CorrelationId | 
| Input-ID | 
| Input-Location | 
    
## Classes implementing the Sink interface and the emit method
### Core Event
The routeEvent method has an input CoreEvent parameter and it returns a CoreEvent.
    
| Key |
| --- |
| Input-CorrelationId | 
| Input-ID | 
| Input-Location | 
| Returned-CorrelationId | 
| Returned-ID | 
| Returned-Location | 
   
## Class InvokerMessageProcessor and process method
Tracks the input parameter CoreEvent   
   
| Key |
| --- |
| CorrelationId | 
| ID | 
| Location | 
   
## Classes that extend AbstractMessageProcessorChain process method

The process method has an input CoreEvent parameter and returns an output CoreEvent.  The key name is prefixed with the direction and a '-'   
   
| Key |
| --- |
| Input-CorrelationId | 
| Input-ID | 
| Input-Location | 
| Returned-CorrelationId | 
| Returned-ID | 
| Returned-Location | 
    
## Classes that implement MessageProcessPhase runPhase method
    
| Key |
| --- |
| Location | 
   
## Classes that extend AbstractExecutableComponent execute method
    
| Key |
| --- |
| CorrelationId | 
| ID | 
| Location | 
   
## Class MessageProcessors processWithChildContext method
   
   
| Key |
| --- |
| Input-CorrelationId | 
| Input-ID | 
| Input-Location | 
   

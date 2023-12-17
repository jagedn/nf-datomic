package com.nextflow.plugin

import datomicJava.client.api.sync.Connection
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import nextflow.Session
import nextflow.processor.TaskHandler
import nextflow.processor.TaskProcessor
import nextflow.trace.TraceObserver
import nextflow.trace.TraceRecord

import java.nio.file.Path
import static datomic.Util.list
import static datomic.Util.map
import static datomic.Util.read

@Slf4j
@CompileStatic
class DatomicTraceObserver implements TraceObserver{

    private Session session
    private Connection connection

    DatomicTraceObserver(Connection connection) {
        this.connection = connection
    }

    @Override
    void onFlowCreate(Session session) {
        log.debug "On FlowCreate"
        this.session = session
        connection.transact(list(
                map(
                        read(":log/session-id"), "$session.uniqueId",
                        read(":log/run-name"), "$session.runName",
                        read(":log/project-name"), "$session.workflowMetadata.projectName",
                        read(":log/run-id"), "$session.uniqueId",
                        read(":log/event"), read(":flow-create"),
                )
        ))
    }

    @Override
    void onFlowBegin() {
        log.debug "On FlowBegin"
        connection.transact(list(
                map(
                        read(":log/session-id"), "$session.uniqueId",
                        read(":log/run-name"), "$session.runName",
                        read(":log/project-name"), "$session.workflowMetadata.projectName",
                        read(":log/run-id"), "$session.uniqueId",
                        read(":log/event"), read(":flow-begin"),
                )
        ))
    }

    @Override
    void onFlowComplete() {
        log.debug "On FlowBegin"
        connection.transact(list(
                map(
                        read(":log/session-id"), "$session.uniqueId",
                        read(":log/run-name"), "$session.runName",
                        read(":log/project-name"), "$session.workflowMetadata.projectName",
                        read(":log/run-id"), "$session.uniqueId",
                        read(":log/event"), read(":flow-complete"),
                )
        ))
    }

    @Override
    void onProcessCreate(TaskProcessor process) {
        log.debug "On ProcessCreate $process.name"
        connection.transact(list(
                map(
                        read(":log/session-id"), "$session.uniqueId",
                        read(":log/run-name"), "$session.runName",
                        read(":log/project-name"), "$session.workflowMetadata.projectName",
                        read(":log/run-id"), "$session.uniqueId",
                        read(":log/event"), read(":process-create"),
                        read(":log/process-name"), "$process.name",
                        read(":log/process-id"), "$process.id",
                )
        ))
    }

    @Override
    void onProcessTerminate(TaskProcessor process) {
        log.debug "On ProcessTerminate $process.name"
        connection.transact(list(
                map(
                        read(":log/session-id"), "$session.uniqueId",
                        read(":log/run-name"), "$session.runName",
                        read(":log/project-name"), "$session.workflowMetadata.projectName",
                        read(":log/run-id"), "$session.uniqueId",
                        read(":log/event"), read(":process-terminate"),
                        read(":log/process-name"), "$process.name",
                        read(":log/process-id"), "$process.id",
                )
        ))
    }

    @Override
    void onProcessPending(TaskHandler handler, TraceRecord trace) {
    }

    @Override
    void onProcessSubmit(TaskHandler handler, TraceRecord trace) {

    }

    @Override
    void onProcessStart(TaskHandler handler, TraceRecord trace) {

    }

    @Override
    void onProcessComplete(TaskHandler handler, TraceRecord trace) {

    }

    @Override
    void onProcessCached(TaskHandler handler, TraceRecord trace) {

    }

    @Override
    boolean enableMetrics() {
        return false
    }

    @Override
    void onFlowError(TaskHandler handler, TraceRecord trace) {
        log.debug "On FlowError $trace.processName"
        connection.transact(list(
                map(
                        read(":log/session-id"), "$session.uniqueId",
                        read(":log/run-name"), "$session.runName",
                        read(":log/project-name"), "$session.workflowMetadata.projectName",
                        read(":log/run-id"), "$session.uniqueId",
                        read(":log/event"), read(":flow-error"),
                        read(":log/process-name"), "$trace.processName",
                )
        ))
    }

    @Override
    void onFilePublish(Path destination) {

    }

    @Override
    void onFilePublish(Path destination, Path source) {

    }
}

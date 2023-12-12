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
    DatomicTraceObserver(Session session, Connection connection) {
        this.session = session
        this.connection = connection
    }

    @Override
    void onFlowCreate(Session session) {
        log.debug "On FlowCreate"
        connection.transact(list(
                map(
                        read(":log/session-id"), "$session.uniqueId",
                        read(":log/run-name"), "$session.runName",
                        read(":log/project-name"), "$session.workflowMetadata.projectName",
                )
        ))
    }

    @Override
    void onFlowBegin() {

    }

    @Override
    void onFlowComplete() {

    }

    @Override
    void onProcessCreate(TaskProcessor process) {

    }

    @Override
    void onProcessTerminate(TaskProcessor process) {

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

    }

    @Override
    void onFilePublish(Path destination) {

    }

    @Override
    void onFilePublish(Path destination, Path source) {

    }
}

package com.nextflow.plugin

import datomicJava.client.api.sync.Connection
import datomicJava.client.api.sync.Client
import groovy.transform.CompileStatic
import nextflow.Session
import nextflow.trace.TraceObserver
import nextflow.trace.TraceObserverFactory

@CompileStatic
class DatomicTraceFactory implements TraceObserverFactory{

    @Override
    Collection<TraceObserver> create(Session session) {
        DatomicConfiguration configuration = new DatomicConfiguration(session.config.navigate('datomic') as Map)
        if( !configuration.enabled ){
            return Collections.emptyList()
        }
        final ret = new ArrayList<TraceObserver>()
        DatomicTraceObserver observer = createDatomicTracaeObserver(session, configuration)
        ret.add(observer)
        return ret
    }

    DatomicTraceObserver createDatomicTracaeObserver(Session session, DatomicConfiguration configuration){
        Client client = DatomicClientFactory.instance.fromConfiguration(configuration)
        Connection cnn = client.connect(configuration.database)
        new DatomicTraceObserver(cnn)
    }

}

package com.nextflow.plugin

import nextflow.plugin.extension.PluginExtensionPoint
import nextflow.Session
import datomicJava.client.api.sync.*


class DatomicExtensionPoint extends PluginExtensionPoint{

    private Session session
    private DatomicConfiguration configuration
    private Client client

    @Override
    protected void init(Session session) {
        this.session = session
        this.configuration = new DatomicConfiguration(session.config.navigate('datomic') as Map)
        if( !this.configuration.enabled ){
            return
        }
        this.client = DatomicClientFactory.instance.fromConfiguration(this.configuration)
    }


}

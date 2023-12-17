package com.nextflow.plugin

import groovy.transform.PackageScope

@PackageScope
class DatomicConfiguration {

    final boolean enabled
    final String system
    final String storage
    final String accessKey
    final String secret
    final String uri
    final String database

    DatomicConfiguration(Map map) {
        def config = map ?: Collections.emptyMap()
        enabled = config.enabled as boolean
        if( !enabled ){
            return
        }
        if (!['dev-local', 'peer-server'].contains(config.system)) {
            throw new RuntimeException("Unknow Datomic system $config.system. Valid values are dev-local | peer-server")
        }

        system = config.system
        database = config.database ?: 'nextflow'

        if (system == 'dev-local') {
            def local = config.devLocal as Map ?: Collections.emptyMap()
            storage = local.storage ?: '.'
        } else {
            def peer = config.peerServer as Map ?: Collections.emptyMap()
            accessKey = peer.accessKey ?: ''
            secret = peer.secret ?: ''
            uri = peer.uri ?: 'localhost:4334'
        }
    }

}

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

    DatomicConfiguration(Map map){
        def config = map ?: Collections.emptyMap()
        enabled = config.enabled as boolean
        system = config.system ?: 'dev-local'
        storage = config.storage ?: '.datomic/data'
        accessKey = config.accessKey ?: ''
        secret = config.secret ?: ''
        uri = config.uri ?: 'localhost:4334'
        database = config.database ?: 'nextflow'
    }

}

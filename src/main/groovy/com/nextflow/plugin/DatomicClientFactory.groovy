package com.nextflow.plugin

import datomicJava.client.api.sync.Client
import datomicJava.client.api.sync.Datomic
import groovy.transform.PackageScope

import java.nio.file.Paths

import static datomic.Util.list
import static datomic.Util.map
import static datomic.Util.read

@Singleton
@PackageScope
class DatomicClientFactory {

    Client fromConfiguration(DatomicConfiguration configuration){
        Client client = configuration.system == 'dev-local' ? prepareDevLocal(configuration) : preparePeerServer(configuration)

        if( !client.listDatabases().contains(configuration.database)) {
            client.createDatabase(configuration.database)
        }

        configuration.system == 'dev-local' ? prepareDevSchema(configuration, client) : preparePeerSchema(configuration, client)

        return client
    }


    Client prepareDevLocal(DatomicConfiguration configuration){
        def local = Paths.get(System.getProperty("user.home"), ".datomic", "local.edn")
        if( !local.exists() ){
            local.parent.mkdirs()
            def dir = Paths.get(System.getProperty("user.home"), ".datomic", "data").toAbsolutePath().toString()
            local.text = """
            {:storage-dir \"$dir\"}
            """.stripMargin()
        }
        Datomic.clientDevLocal(configuration.storage)
    }

    Client preparePeerServer(DatomicConfiguration configuration){
        Datomic.clientPeerServer(configuration.accessKey, configuration.secret, configuration.uri)
    }

    void prepareDevSchema(DatomicConfiguration configuration, Client client) {

        def db = client.connect(configuration.database)

        db.transact(list(
                map(
                        read(":db/ident"), read(":log/session-id"),
                        read(":db/valueType"), read(":db.type/string"),
                        read(":db/cardinality"), read(":db.cardinality/one"),
                        read(":db/doc"), "The session Id"
                ),
                map(
                        read(":db/ident"), read(":log/run-name"),
                        read(":db/valueType"), read(":db.type/string"),
                        read(":db/cardinality"), read(":db.cardinality/one"),
                        read(":db/doc"), "The run name"
                ),
                map(
                        read(":db/ident"), read(":log/project-name"),
                        read(":db/valueType"), read(":db.type/string"),
                        read(":db/cardinality"), read(":db.cardinality/one"),
                        read(":db/doc"), "The project name"
                ),
                map(
                        read(":db/ident"), read(":log/event"),
                        read(":db/valueType"), read(":db.type/keyword"),
                        read(":db/cardinality"), read(":db.cardinality/one"),
                        read(":db/doc"), "The event"
                ),
                map(
                        read(":db/ident"), read(":log/run-id"),
                        read(":db/valueType"), read(":db.type/string"),
                        read(":db/cardinality"), read(":db.cardinality/one"),
                        read(":db/doc"), "The run id"
                ),
                map(
                        read(":db/ident"), read(":log/process-id"),
                        read(":db/valueType"), read(":db.type/string"),
                        read(":db/cardinality"), read(":db.cardinality/one"),
                        read(":db/doc"), "The run id"
                ),
                map(
                        read(":db/ident"), read(":log/process-name"),
                        read(":db/valueType"), read(":db.type/string"),
                        read(":db/cardinality"), read(":db.cardinality/one"),
                        read(":db/doc"), "The run id"
                ),
                map(
                        read(":db/ident"), read(":log/project+session+process"),
                        read(":db/valueType"), read(":db.type/tuple"),
                        read(":db/tupleAttrs"),
                            read("[:log/project-name :log/session-id :log/process-id :log/event]"),
                        read(":db/cardinality"), read(":db.cardinality/one"),
                        read(":db/unique"), read(":db.unique/identity"),
                        read(":db/doc"), "The run id",
                ),
        ))

    }

    void preparePeerSchema(DatomicConfiguration configuration, Client client) {
        prepareDevSchema(configuration, client)
    }
}

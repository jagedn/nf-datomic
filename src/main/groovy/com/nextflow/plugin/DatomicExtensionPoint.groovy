package com.nextflow.plugin

import nextflow.plugin.extension.Function
import nextflow.plugin.extension.PluginExtensionPoint
import nextflow.Session
import datomicJava.client.api.sync.*

import java.nio.file.Paths

import static datomic.Util.*;

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

        this.client = configuration.system == 'dev-local' ? prepareDevLocal() : preparePeerServer()

        if( !client.listDatabases().contains(configuration.database)) {
            client.createDatabase(configuration.database)
            configuration.system == 'dev-local' ? prepareDevSchema() : preparePeerSchema()
        }
    }

    Client prepareDevLocal(){
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

    Client preparePeerServer(){
        Datomic.clientPeerServer(configuration.accessKey, configuration.secret, configuration.uri)
    }

    void prepareDevSchema() {

        def db = client.connect(configuration.database)


        def schema = db.transact(list(
                map(
                        read(":db/ident"), read(":movie/title"),
                        read(":db/valueType"), read(":db.type/string"),
                        read(":db/cardinality"), read(":db.cardinality/one"),
                        read(":db/doc"), "The title of the movie"
                ),
                map(
                        read(":db/ident"), read(":movie/genre"),
                        read(":db/valueType"), read(":db.type/string"),
                        read(":db/cardinality"), read(":db.cardinality/one"),
                        read(":db/doc"), "The genre of the movie"
                ),
                map(
                        read(":db/ident"), read(":movie/release-year"),
                        read(":db/valueType"), read(":db.type/long"),
                        read(":db/cardinality"), read(":db.cardinality/one"),
                        read(":db/doc"), "The year the movie was released in theaters"
                )
        )).txData()


        def films = db.transact(list(
                map(
                        read(":movie/title"), "The Goonies",
                        read(":movie/genre"), "action/adventure",
                        read(":movie/release-year"), 1985
                ),
                map(
                        read(":movie/title"), "Commando",
                        read(":movie/genre"), "thriller/action",
                        read(":movie/release-year"), 1985
                ),
                map(
                        read(":movie/title"), "Repo Man",
                        read(":movie/genre"), "punk dystopia",
                        read(":movie/release-year"), 1984
                )
        )).txData()

    }

    void preparePeerSchema() {
        prepareDevSchema()
    }

    @Function
    List<String> movies(){

        def db = client.connect(configuration.database)

        def result = Datomic.q('[:find ?movie-title :where [_ :movie/title ?movie-title]]',db.db())
        result.collect{
            "The movie ${it[0]}"
        }
    }

}

package com.nextflow.plugin

import datomicJava.client.api.sync.Datomic
import nextflow.Channel
import nextflow.plugin.Plugins
import nextflow.plugin.TestPluginDescriptorFinder
import nextflow.plugin.TestPluginManager
import nextflow.plugin.extension.PluginExtensionProvider
import org.pf4j.PluginDescriptorFinder
import spock.lang.Ignore
import spock.lang.Shared
import test.Dsl2Spec
import test.MockScriptRunner

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class PluginTest extends Dsl2Spec{

    @Shared String pluginsMode

    def setup() {
        // reset previous instances
        PluginExtensionProvider.reset()
        // this need to be set *before* the plugin manager class is created
        pluginsMode = System.getProperty('pf4j.mode')
        System.setProperty('pf4j.mode', 'dev')
        // the plugin root should
        def root = Path.of('.').toAbsolutePath().normalize()
        def manager = new TestPluginManager(root){
            @Override
            protected PluginDescriptorFinder createPluginDescriptorFinder() {
                return new TestPluginDescriptorFinder(){
                    @Override
                    protected Path getManifestPath(Path pluginPath) {
                        return pluginPath.resolve('build/tmp/jar/MANIFEST.MF')
                    }
                }
            }
        }
        Plugins.init(root, 'dev', manager)
    }

    def cleanup() {
        Plugins.stop()
        PluginExtensionProvider.reset()
        pluginsMode ? System.setProperty('pf4j.mode',pluginsMode) : System.clearProperty('pf4j.mode')
    }

    def 'should starts' () {
        when:
        def SCRIPT = '''
            channel.of('hi!') 
            '''
        and:
        def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
        result.val == 'hi!'
        result.val == Channel.STOP
    }

    def 'should create default datomic data storage' () {
        given:
        def local = Paths.get(System.getProperty("user.home"), ".datomic")
        local.deleteDir()

        def map = [
                datomic:[
                        enabled:true, system:'dev-local', database:'tmp'
                ]
        ]
        def client = DatomicClientFactory.instance.fromConfiguration(new DatomicConfiguration(map.datomic))
        client.deleteDatabase('tmp')

        when:
        def SCRIPT = '''
            def movies = ['comando', 'the gonnies']            
            channel.from( movies )      
            '''
        and:
        def result = new MockScriptRunner(map).setScript(SCRIPT).execute()

        then:
        result.val
        result.val
        result.val == Channel.STOP

        and:
        Datomic.q("[:find ?session-id :where [_ :log/session-id ?session-id ]]", client.connect("tmp").db()).size()
    }

}

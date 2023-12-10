package com.nextflow.plugin

import clojure.lang.RT
import groovy.util.logging.Slf4j
import nextflow.plugin.BasePlugin
import org.pf4j.PluginWrapper


@Slf4j
class DatomicPlugin extends BasePlugin{

    DatomicPlugin(PluginWrapper wrapper) {
        super(wrapper)
        initPlugin()
    }

    private void initPlugin(){
        log.info "${this.class.name} plugin initialized"
        Thread.currentThread().setContextClassLoader(wrapper.pluginClassLoader)
        RT.makeClassLoader()
    }
}

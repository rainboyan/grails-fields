package grails.plugin.formfields

import grails.boot.Grails
import grails.plugins.metadata.PluginSource
import groovy.transform.CompileStatic

@CompileStatic
@PluginSource
class Application {

    static void main(String[] args) {
        Grails.run(Application, args)
    }
}
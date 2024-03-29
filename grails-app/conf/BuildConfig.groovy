grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [
		// configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
		//  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

		// configure settings for the test-app JVM, uses the daemon by default
		test   : [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256/*, daemon: true*/],
		// configure settings for the run-app JVM
		run    : [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],
		// configure settings for the run-war JVM
		war    : [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],
		// configure settings for the Console UI JVM
		console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
	// inherit Grails' default dependencies
	inherits("global") {
		// specify dependency exclusions here; for example, uncomment this to disable ehcache:
		// excludes 'ehcache'
	}
	log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	checksums true // Whether to verify checksums on resolve
	legacyResolve false
	// whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

	repositories {
		inherits true // Whether to inherit repository definitions from plugins

		mavenRepo ([url: "http://maven-public.causecode.com"])
		grailsPlugins()
		grailsHome()
		mavenLocal()
		grailsCentral()
		mavenCentral()
	}

	dependencies {
		test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"
		// Dependency for spring security core plugin.
		compile "net.sf.ehcache:ehcache-core:2.4.8"

		/*
		 * Grails somehow shipping older version of Java driver i.e. 2.12.3 and to support MongoDB 3, we require
		 * minimum 2.13.x of Java driver. Also, the 3.0 Java driver is not a required upgrade for MongoDB 3.0.
		 * The 2.13.0 release is the minimum required for full compatibility with MongoDB 3.0.
		 * https://github.com/mongodb/mongo-java-driver/releases/tag/r3.0.0
		 */
		compile "org.mongodb:mongo-java-driver:2.13.1"
		compile 'org.codehaus.groovy.modules.http-builder:http-builder:0.7.1'
		// http://stackoverflow.com/questions/28394699/grails-exception-while-run-war-nosuchmethoderror
		build "com.google.guava:guava:18.0"
	}

	plugins {
		// plugins for the build system only
		build ":tomcat:8.0.22"

		// plugins for the compile step
		compile ":asset-pipeline:2.1.5"
		compile ":database-session:1.2.2-CC"
		compile ":spring-security-core:2.0-RC4"
		compile ":spring-security-oauth2-provider:2.0-RC4"
		compile ":mail:1.0.7"
		compile ":mongodb:3.0.3"
		compile ":quartz:1.0.2"
		compile (":mongeez:0.2.3") {
			excludes("mongo-java-driver")
		}
		compile "org.grails.plugins:csv:0.3.1"
		compile ":webxml:1.4.1"

		// Uncomment these to enable additional asset-pipeline capabilities
		//compile ":sass-asset-pipeline:1.9.0"
		//compile ":less-asset-pipeline:1.10.0"
		//compile ":coffee-asset-pipeline:1.8.0"
		//compile ":handlebars-asset-pipeline:1.3.0.3"
	}
}

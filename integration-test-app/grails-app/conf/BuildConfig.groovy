grails.servlet.version = '3.0'
grails.project.work.dir = 'target'
grails.project.target.level = 1.7
grails.project.source.level = 1.7

grails.project.dependency.resolver = 'maven'
grails.project.dependency.resolution = {
	inherits 'global'
	log 'warn'
	checksums true
	legacyResolve false

	repositories {
		inherits true

		mavenLocal()
		grailsCentral()
		mavenCentral()
	}

	dependencies {
		compile "org.springframework:spring-orm:$springVersion"
	}

	plugins {
		runtime ':spring-security-core:2.0.0'
		runtime ':hibernate4:4.3.5.4'
	}
}

# FreeLib-MARC4J [![Build Status](https://travis-ci.org/ksclarke/freelib-marc4j.png?branch=master)](https://travis-ci.org/ksclarke/freelib-marc4j)

_**Deprecation Notice:**_ This fork was started because I needed a version of MARC4J in Maven Central. Before I forked, I'd submitted a PR to the upstream project that reworked the project as a Maven (rather 
than Ant) project, but there wasn't any interest in adopting Maven by the upstream developers. So, I forked and started working on this project, freelib-marc4j.

Just recently, the upstream project has reconfigured their Ant build so that they can publish artifacts into Maven Central. They've also merged in my freelib-marc4j additions (with the exception of the Maven structure). As a result, I don't 
really see a reason to continue development on this fork. Having the project in the Maven structure would be nice, but it's not essential to me. I really just needed a build artifact published into Maven 
Central.

I'll keep this project around on GitHub, for awhile at least, but I'm marking it as deprecated since I'm going back to using the upstream MARC4J (and so won't be doing any future development on 
freelib-marc4j). Thanks to the folks who submitted PRs to freelib-marc4j while it was around (through [10 releases](http://mvnrepository.com/artifact/info.freelibrary/freelib-marc4j)). I really appreciate your contributions!

### Getting Started

The project page has more detail, but if you'd like to jump right in (and already have [Maven](http://maven.apache.org) installed), clone this project from [its GitHub page](http://github.com/ksclarke/freelib-marc4j):

    git clone http://github.com/ksclarke/freelib-marc4j

change into its project directory:

    cd freelib-marc4j

and type:

    mvn install

This will compile the project and run its tests.  It will install a jar file into your local Maven repository and also leave a copy in the project's target directory (in case you don't care about Maven and just want the jar file artifact).  If you want to recompile, you can run the above command again.  If you want a completely clean build, you can run:

    mvn clean install

This will delete the whole 'target' folder (the product of running a Maven build) and will create a new one.

If you want to create the Javadocs for the project, you can type:

    mvn javadoc:javadoc

and they will be created in `freelib-marc4j/target/site/apidocs`

If you just want to include MARC4J as a dependency in your own Maven-managed project, you can by adding the following to your project's dependencies:

    <dependency>
        <groupId>info.freelibrary</groupId>
        <artifactId>freelib-marc4j</artifactId>
        <version>2.6.12</version>
    </dependency>


### Contact

If you have a question about the FreeLib-MARC4J project, feel free to ask it on the FreeLibrary Projects <a href="https://groups.google.com/forum/#!members/freelibrary-projects">mailing list</a>.  If you've found a bug in the code (or have a suggestion for how it could be improved), please [open a new issue](https://github.com/ksclarke/freelib-marc4j/issues "GitHub Issues Queue") in the project's GitHub issues queue.  Lastly, feel free to <a href="mailto:ksclarke@gmail.com">contact me</a> directly.

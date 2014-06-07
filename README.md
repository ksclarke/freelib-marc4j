# FreeLib-MARC4J [![Build Status](https://travis-ci.org/ksclarke/freelib-marc4j.png?branch=master)](https://travis-ci.org/ksclarke/freelib-marc4j)

This is a fork of the MARC4J project. It is designed to be used by other software projects that use Maven as a project management tool.  For now, it aims to be compatible with the original MARC4J's public-facing API.

For more detailed information about this fork, read its [documentation](http://projects.freelibrary.info/freelib-marc4j/).

### TL;DR

Is the official documentation too much to read?  If you just want to jump right in (and already have [Maven](http://maven.apache.org) installed), clone this project from [its GitHub page](http://github.com/ksclarke/freelib-marc4j):

    git clone http://github.com/ksclarke/freelib-marc4j

change into its project directory:

    cd freelib-marc4j

and type:

    mvn install

This will compile the project and run its tests.  It will install a jar file into your local Maven repository and also leave one in the 'target' folder (in case you don't care about Maven and just want the jar file artifact).  If you want to recompile/rebuild, you can run the above command again.  If you want a completely clean build, you can type:

    mvn clean install

This will delete the whole target folder (the product of running Maven) and create a new one.

If you want to create the Javadocs for the project, you can type:

    mvn javadoc:javadoc

and they will be created in `freelib-marc4j/target/site/apidocs`

If you want to create the site that you see at http://projects.freelibrary.info/freelib-marc4j/ you can type:

    mvn site
 
and it will be created in `freelib-marc4j/target/site/`

Of course, you can also just include it as a dependency in your Maven managed project by adding the following to your project's dependencies:

    <dependency>
        <groupId>info.freelibrary</groupId>
        <artifactId>freelib-marc4j</artifactId>
        <version>2.6.3</version>
    </dependency>


### Contact

If you have a question about the FreeLib-MARC4J project, feel free to ask it on the FreeLibrary Projects <a href="https://groups.google.com/forum/#!members/freelibrary-projects">mailing list</a>.  If you've found a bug in the code (or have a suggestion for how it could be improved), please [open a new issue](https://github.com/ksclarke/freelib-marc4j/issues "GitHub Issues Queue") in the project's GitHub issues queue.  Lastly, feel free to <a href="mailto:ksclarke@gmail.com">contact me</a> directly.

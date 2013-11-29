# FreeLib-MARC4J

This is a Mavenized fork of the MARC4J project.  It's a place for me to do some experimentation.  For now, it aims to be compatible with the public API of the [original MARC4J project](http://github.com/marc4j/marc4j).  For more detailed information about this fork, read its [documentation](http://projects.freelibrary.info/freelib-marc4j/).

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

So, poke around... experiment, and if you have any problems or questions, [feel free to contact me](mailto:ksclarke@gmail.com).

### Project Status

[![master branch](https://travis-ci.org/ksclarke/freelib-marc4j.png?branch=master)](https://travis-ci.org/ksclarke/freelib-marc4j)

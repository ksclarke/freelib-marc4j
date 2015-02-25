### Mission

The goal of MARC4J is to provide an easy to use API for working with MARC and MARCXML records in Java. **MARC** stands for MAchine Readable Cataloging. It is a widely used exchange format for bibliographic data. **MARCXML** provides a loss-less conversion between MARC (MARC21, in particular, but also formats like UNIMARC) and XML (the eXtensible Markup Language).

The primary mission of the FreeLib-MARC4J project is to make the MARC4J library available through the central Maven repository.  It is a fork (and will diverge from the upstream project), but it will also attempt to continue to support the upstream MARC4J project's public API.

### Background

The current MARC4J is an object-oriented software library.  Releases beta 6 through beta 8a, however, were based on an event based parser, like SAX for XML. The project started as James (Java MARC events) but, since there was already an open source project called James, the project was renamed to MARC4J to avoid confusion in the open source community.  The project was started by Bas Peters, but others in the library community have since assumed responsibility for its ongoing maintenance.

### Features

The MARC4J library includes:

* An easy to use interface that can handle large record sets.
* Readers and writers for both MARC and MARCXML.
* A build-in pipeline model to pre- or post-process MARCXML using XSLT stylesheets.
* A MARC record object model (like DOM for XML) for in-memory editing of MARC records.
* Support for data conversions from MARC-8 ANSEL, ISO5426 or ISO6937 to UCS/Unicode and back.
* A forgiving reader which can handle and recover from a number of structural or encoding errors in records.
* Implementation independent XML support through JAXP and SAX2, a high performance XML interface.
* Support for conversions between MARC and MARCXML.
* Tight integration with the JAXP, DOM and SAX2 interfaces.

MARC4J provides readers and writers for MARC and MARCXML. A `org.marc4j.MarcReader` implementation parses input data and provides an iterator over a collection of `org.marc4j.marc.Record` objects. The record object model is also suitable for in-memory editing of MARC records, just as DOM is used for XML editing purposes. Using a `org.marc4j.MarcWriter` implementation it is possible to create MARC or MARCXML. Once MARC data has been converted to XML you can further process the result with XSLT, for example to convert MARC to MODS.

Although MARC4J is primarily designed for Java development you can use the command-line utilities `org.marc4j.util.MarcXmlDriver` and `org.marc4j.util.XmlMarcDriver` to convert between MARC and MARCXML. It is also possible to pre- or post-process the result using XSLT, for example to convert directly from MODS to MARC or from MARC to MODS.

### Getting Started

To get started with MARC4J, you can either: 1) clone the GitHub repository, 2) download the source code, or 3) include FreeLib-MARC4J as a dependency in your Maven-managed project.

Cloning the repository and compiling the code yourself is easy.  The source is available on GitHub (so you will need Git installed). Maven is used to compile and install the jar in your local Maven repository (so you will need Apache [Maven](http://maven.apache.org/) installed on your local machine, too).  The steps to clone and build are:

    git clone https://github.com/ksclarke/freelib-marc4j.git
    cd freelib-marc4j
    mvn install

If you just want the jar (and don't care about a local Maven repository, you can find it in the project's "target" folder).  To recompile the project, you can rerun: mvn install.  If you want a completely clean build, you can run: mvn clean install.

If you would like to download the source code, releases are available for download from the GitHub project's [download page](http://github.com/ksclarke/freelib-marc4j/releases).  You can follow the above steps (minus the cloning) to build the project and install it in your local Maven repository.

Lastly, if you are using Maven, you can also just include MARC4J as a dependency in your project.  To do this, add the following markup to your pom.xml file's dependencies:

    <dependency>
	    <groupId>info.freelibrary</groupId>
	    <artifactId>freelib-marc4j</artifactId>
	    <version>2.6.5</version>
    </dependency>

It's that simple.

### Publications and Other Articles of Interest

* [Crosswalking: Processing MARC in XML Environments with MARC4J](http://www.amazon.com/Crosswalking-Processing-MARC-Environments-MARC4J/dp/1847530281)
* [A Proposal to serialize MARC in JSON](http://dilettantes.code4lib.org/blog/2010/09/a-proposal-to-serialize-marc-in-json/)
* [MARC-JSON Draft 2010-03-11](http://www.oclc.org/developer/content/marc-json-draft-2010-03-11)
* [Format for Information Exchange](http://www.niso.org/kst/reports/standards?step=2&gid=&project_key=fb7a107043228a342cb704973825aca7bc6ae58d)
* [MARC21](http://www.loc.gov/marc/)
* [UNIMARC](http://www.ifla.org/publications/unimarc-formats-and-related-documentation)
* [MARCXML](http://www.loc.gov/standards/marcxml/)
* [SAX2](http://www.saxproject.org)
* [JAXP](https://jaxp.java.net/)

### Questions?

If you have a question about the FreeLib-MARC4J project, feel free to ask it on the FreeLibrary Projects <a href="https://groups.google.com/forum/#!members/freelibrary-projects">mailing list</a>.  If you've found a bug in the code (or have a suggestion for how it could be improved), please [open a new issue](https://github.com/ksclarke/freelib-marc4j/issues "GitHub Issues Queue") in the project's GitHub issues queue.  Lastly, feel free to <a href="mailto:ksclarke@gmail.com">contact me</a> directly with any FreeLib-MARC4J concerns.

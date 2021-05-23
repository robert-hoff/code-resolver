

# Java Code Resolver Tool
This tool is used for Codeforces competitions. Resolves external dependencies on a target Java source file,
combining sources into a single class suitable for submission.


## Installation
Download the project into any directory. Import the project in Eclipse as 'Existing Maven Project'


## Usage
Run `MakeTemplates` to set up source files in `/dev`. Solve Codeforces problems in these files.

Run `CodeResolverUI` to resolve and package source files into a format suitable for submission.
The target result is written to the Java base folder `src/main/java` and will not belong to any package.
The source is also copied to clipboard.


## Features
Call static methods in `extmethods.*` and instantiate classes from `extclasses.*`

Create new static methods in `extmethods.*` (nested methods calls are supported)

Create new custom classes in `extclasses.*` (be aware nested calls not supported for classes)


## Logger
To configure the logger copy `/src/main/resources/logback-template.xml` into `/src/main/resources/logback.xml`
(for details, see http://logback.qos.ch/manual/configuration.html)







# Java Code Resolver Tool
Resolves external dependecies on a target source file, combining sources into a single class.


## Installation
Import project in Eclipse as 'Existing Maven Project'


## Usage
Run MakeTemplates to set up compilation targets in `/dev`

Run CodeResolverUI to resolve source files.


## Features
Call static methods in `extmethods.*` and instantiate classes from `extclasses.*`

Create new static methods in `extmethods.*` (nested methods calls are supported)
Calls to methods from the target source file will be resolved.

Create classes in `extclasses.*` (be aware nested calls not supported for classes)
Objects declarations from the target source file will be resolved.


## Logger
To configure the logger copy `/src/main/resources/logback-template.xml` into `/src/main/resources/logback.xml`
(logback.xml contains the Logback configuration, see http://logback.qos.ch/manual/configuration.html)





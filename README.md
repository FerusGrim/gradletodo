GradleTodo [![Build Status](https://travis-ci.org/FerusGrim/gradletodo.svg?branch=master)](https://travis-ci.org/FerusGrim/gradletodo)
==========

GradleTodo is a Gradle plugin simply designed to show comments in your source marked as TODO.

## Installation

### Compatible with all Gradle versions:

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.ferusgrim:GradleTodo:1.0.6"
  }
}

apply plugin: "com.ferusgrim.gradletodo"
```

### Compatible with Gradle 2.1+:

```groovy
plugins {
  id "com.ferusgrim.gradletodo" version "1.0.6"
}
```

## Running

Gradle Task: `todo`

## Example Output
```stack
:todo

1 TODO found in `GrimList.java`
L44: This can be ignored.

2 TODOs found in `GrimLog.java`
L42: Add additional ModType's
L62: Return non-null value

3 TODOs found in `ConfigKey.java`
L14: Mark all mysql values
L48: return non-null value
L81: condense this?
```
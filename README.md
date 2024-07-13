# Functional Utils

## Description

This library aims to provide a set of utilities to help with functional programming in Java. 

It is inspired by the functional programming libraries in other languages, such as Scala, Haskell, and Clojure and aims 
to be a zero-dependencies library.

The main advantage of libraries like `Vavr` or `Cyclops` is that:
1. They do not seem to be maintained anymore.
2. They are not zero-dependencies libraries.
3. They have a big learning curve.

## Features

1. `Either` monad which represents a value that can be either of two types.
2. `Try` monad which represents an operation that can either succeed or fail.
3. `EitherT` monad transformer which allows to encapsulate an `Either` monad inside a CompletableFuture. This allows chaining and composing asynchronous computations that may
fail, using functional programming principles.
4. `TryT` monad transformer which allows to encapsulate a `Try` monad inside a CompletableFuture. This allows chaining and composing asynchronous computations that may fail, using functional programming principles.
5. More features coming soon.

## Installation guide

To add the library to your project, you need to add the jitpack repository to your build file.

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

```

Then, you can add the dependency to your project.

```xml
<dependency>
  <groupId>com.github.VassilisSoum</groupId>
  <artifactId>FunctionalUtils</artifactId>
  <version>1.0.0</version>
</dependency>
```
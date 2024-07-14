# Functional Utils

## Description

This library aims to provide a set of utilities to help with functional programming in Java. 

It is inspired by the functional programming libraries in other languages, such as Scala, Haskell, and Clojure and aims 
to be a zero-dependencies library.

It is also inspired by libraries such as `Vavr` or `Cyclops`. But these have issues such as:
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
  <version>1.2.1</version>
</dependency>
```

## TODO
1. Validated which is a nice way to accummulate all errors happening in a context.
2. Eval monad to allow for lazy and memoized evaluated computation, eagerly evaluated computation and always evaluated computation.
3. Persistent List monad implementation equivalent to ArrayList but different from the Java collections to be truly immutable and performant using structural sharing.
4. Persistent HashMap/HashSet monad implementation but different from the Java collections to be truly immutable and performant using structural sharing.
5. Memoized version of CompletableFuture to ensure a single execution of an instance of CompletableFuture and avoid surprises.
6. Lots of useful types with safe validations such as NonEmptyList, PositiveNumber, NonNegativeNumber, NonEmptyString etc

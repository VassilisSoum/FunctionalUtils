# Functional Utils

## Description

This library aims to provide a set of utilities to help with functional programming in Java. 

It is inspired by the functional programming libraries in other languages, such as Scala, Haskell, and Clojure and aims 
to be a zero-dependencies library.

It is also inspired by libraries such as `Vavr` or `Cyclops`. But these have issues such as:
1. They do not seem to be maintained anymore.
2. They are not zero-dependencies libraries.
3. They have a big learning curve.

It is a small library, and it will never compete the big ones, but it can be useful for those that would like to remain lean.

## Features

1. `Either` monad which represents a value that can be either of two types.
2. `Try` monad which represents an operation that can either succeed or fail.
3. `EitherT` monad transformer which allows to encapsulate an `Either` monad inside a CompletableFuture. This allows chaining and composing asynchronous computations that may
fail, using functional programming principles.
4. `TryT` monad transformer which allows to encapsulate a `Try` monad inside a CompletableFuture. This allows chaining and composing asynchronous computations that may fail, using functional programming principles.
5. `Validated` monad which represents a value that can be either of two types, but it accumulates errors.
6. `Resource` which represents a resource that needs to be managed and closed after use for safe resource management.
7. `For Comprehension` which allows to chain and compose monads in a more readable way than using nested flatMaps.
8. `Lazy` monad which represents a value that is computed lazily and memoized.
9. `SeqList` monad which represents a persistent and immutable list.
10. `Option` monad which represents a value that may or may not be present which is an enhancement over the Java Optional.
11. `Reader` monad which represents a computation that depends on a configuration.
12. `Writer` monad which represents a computation that produces a log.
13. `IO` monad which represents a computation that performs side effects which is lazy.

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
  <version>2.8.0</version>
</dependency>
```

## Articles to read

1. Introducing `TryT` monad with some examples [TryT article](https://www.catnipcoder.com/monad-transformer-in-java-part1)
2. Explaining `SeqList` monad with some examples [SeqList article](https://www.catnipcoder.com/persistent-and-immutable-list)
3. Explaining the `Lazy` monad with some examples [Lazy monad article](https://www.catnipcoder.com/lazy-monad)
4. Explaining the `Option` monad with some examples [Optional monad article](https://www.catnipcoder.com/option-monad)
5. Explaining the `Either` monad with some examples [Either monad article](https://github.com/VassilisSoum/FunctionalUtils/wiki/Either-Monad)

## TODO
1. Persistent List monad implementation equivalent to ArrayList but different from the Java collections to be truly immutable and performant using structural sharing.
2. Persistent HashMap/HashSet monad implementation but different from the Java collections to be truly immutable and performant using structural sharing.

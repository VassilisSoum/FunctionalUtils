package com.soumakis.control;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A resource that can be acquired and released. Useful for managing resources that need to be
 * cleaned up after use.
 *
 * <p>Example usage:</p>
 * <p>
 * Let's define a method that creates a {@link Resource} for reading a file:
 * <pre>
 *   {@code
 *      Resource<BufferedReader> createFileResource(String filePath) {
 *         return Resource.make(
 *             () -> {
 *                 try {
 *                     return Try.of(() -> new BufferedReader(new FileReader(filePath)));
 *                 } catch (IOException e) {
 *                     return Try.of(() -> { throw e; });
 *                 }
 *             },
 *             reader -> {
 *                 try {
 *                     reader.close();
 *                     return null;
 *                 } catch (IOException e) {
 *                     throw new RuntimeException(e);
 *                 }
 *             }
 *         );
 *     }
 *   }
 * </pre>
 * <p>
 *
 * <b>The important distinction from using a try-with-resources directly is that the side effect is not
 * executed until the `use` method is called which makes it lazy evaluated.</b>
 * <p>
 * And then use it like this:
 * <pre>
 *   {@code
 *   Resource<BufferedReader> fileResource = createFileResource("file.txt");
 *   Try<String> result = fileResource.use(reader -> {
 *    return reader.readLine();
 *   }
 *   }
 * </pre>
 * <p>
 * The resource is automatically released after the function is applied.
 *
 * @param <A> the type of the resource
 */
public final class Resource<A> {

  private final Supplier<Try<A>> acquireFn;
  private final Function<A, Void> releaseFn;

  private Resource(Supplier<Try<A>> acquireFn, Function<A, Void> releaseFn) {
    this.acquireFn = acquireFn;
    this.releaseFn = releaseFn;
  }

  /**
   * Creates a new {@link Resource} instance with the provided acquire and release functions.
   *
   * @param acquireFn the function to acquire the resource
   * @param releaseFn the function to release the resource
   * @param <A>       the type of the resource
   * @return a new {@link Resource} instance
   */
  public static <A> Resource<A> make(Supplier<Try<A>> acquireFn,
      Function<A, Void> releaseFn) {
    return new Resource<>(acquireFn, releaseFn);
  }

  /**
   * Acquires the resource and applies the provided function to it. The resource is released after
   * the function is applied.
   *
   * @param useFn the function to apply to the resource
   * @param <B>   the type of the result
   * @return the result of applying the function to the resource
   */
  public <B> Try<B> use(Function<A, B> useFn) {
    return acquireFn.get()
        .flatMap(resource -> {
          try {
            return Try.of(() -> useFn.apply(resource));
          } finally {
            releaseFn.apply(resource);
          }
        });
  }
}

package net.microfalx.registry;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A central interface to access the registry.
 * <p>
 * The registry is a hierarchical representation of data, similar to a tree structure. Each node in the registry can
 * have child nodes and can also carry data. The registry provides methods to list, check existence, retrieve, and
 * set data at specific paths within this hierarchy.
 */
public interface Registry {

    /**
     * Returns an instance of the registry.
     *
     * @return a non-null instance
     */
    static Registry get() {
        return RegistryService.getInstance().getRegistry();
    }

    /**
     * Walks the tree starting with the given path.
     *
     * @param path the path
     * @return a non-null instance
     */
    boolean walk(String path, int depth, BiFunction<String, Node, Boolean> visitor);

    /**
     * Lists all data registered under the specified path.
     *
     * @param path the path
     * @return a non-null instance
     */
    Iterable<Data> list(String path);

    /**
     * Lists all data registered under the specified path.
     *
     * @param path   the path
     * @param filter a filter to apply to the nodes, only nodes for which the filter returns {@code true} will be
     *               included in the result
     * @return a non-null instance
     *
     */
    Iterable<Data> list(String path, Function<Node, Boolean> filter);

    /**
     * Returns whether there is a node in the registry at the given path.
     *
     * @param path the path
     * @return {@code true} if exists, {@code false} otherwise
     */
    boolean exists(String path);

    /**
     * Returns the node available in the registry at a given path.
     *
     * @param path the path
     * @return an optional  data
     */
    Optional<Node> lookup(String path);

    /**
     * Returns the data available in the registry at a given path.
     *
     * @param path the path
     * @return an optional  data
     */
    Optional<Data> get(String path);

    /**
     * Returns the data available in the registry at a given path or creates a new object if needed.
     *
     * @param path the path where the new data object should be retrieved or created
     * @return a non-nul instance
     */
    Data getOrCreate(String path);

    /**
     * Creates a new data object at the specified path in the registry.
     *
     * @param path the path where the new data object should be created
     * @return a non-null instance
     */
    Data create(String path);

    /**
     * Changes the node's data.
     *
     * @param data the new data
     */
    void set(Data data);

    /**
     * Changes the node's data.
     *
     * @param path the path where the new data should be set
     * @param data the new data
     */
    <T> void set(String path, T data);

    /**
     * Returns the storage used by this registry.
     *
     * @return a non-null instance
     */
    Storage getStorage();
}

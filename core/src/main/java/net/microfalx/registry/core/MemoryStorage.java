package net.microfalx.registry.core;

import net.microfalx.lang.StringUtils;
import net.microfalx.lang.UriUtils;
import net.microfalx.lang.annotation.Order;
import net.microfalx.lang.annotation.Provider;
import net.microfalx.registry.Node;
import net.microfalx.registry.RegistryVersionConflictException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static net.microfalx.lang.StringUtils.removeEndSlash;
import static net.microfalx.lang.UriUtils.SLASH;
import static net.microfalx.lang.UriUtils.isRoot;
import static net.microfalx.registry.core.RegistryUtils.normalizePath;

@Order(Order.AFTER)
@Provider
public class MemoryStorage extends AbstractStorage {

    private final Map<String, NodeImpl> nodes = new ConcurrentHashMap<>();

    @Override
    public Collection<Node> getChildren(String path, boolean recursive) {
        String normalizedPath = normalizePath(path);
        return nodes.keySet().stream()
                .filter(nodePath -> isChild(normalizedPath, nodePath, recursive))
                .map(nodes::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Node> getNode(String path) {
        String normalizedPath = normalizePath(path);
        return Optional.ofNullable(nodes.get(normalizedPath));
    }

    @Override
    public boolean exists(String path) {
        String normalizedPath = normalizePath(path);
        NodeImpl node = nodes.get(normalizedPath);
        return node != null && node.isExists();
    }

    @Override
    public byte[] get(String path) {
        String normalizedPath = normalizePath(path);
        NodeImpl node = nodes.getOrDefault(normalizedPath, null);
        return node != null ? node.data : null;
    }

    @Override
    public void put(String path, byte[] data) {
        put(path, data, 0);
    }

    @Override
    public void put(String path, byte[] data, int version) {
        String normalizedPath = normalizePath(path);
        LocalDateTime now = LocalDateTime.now();
        NodeImpl node = nodes.computeIfAbsent(normalizedPath, key -> {
            NodeImpl newNode = new NodeImpl(null, key);
            newNode.setCreatedAt(now);
            return newNode;
        });
        int currentVersion = node.getInternalVersion().get();
        if (version == 0) {
            node.incrementVersion();
        } else {
            if (!node.setVersion(currentVersion, currentVersion + 1)) {
                throw new RegistryVersionConflictException("Version conflict for node '" + normalizedPath
                        + "', current version: " + currentVersion + ", expected version: " + version);
            }
        }
        node.setExists(true);
        node.setLeaf(true);
        node.setUpdatedAt(now);
        node.data = data;
    }

    @Override
    public void remove(String path) {
        String normalizedPath = normalizePath(path);
        nodes.remove(normalizedPath);
    }

    private boolean isChild(String parentPath, String childPath, boolean recursive) {
        if (childPath.equals(parentPath)) return false;
        parentPath = isRoot(parentPath) ? parentPath : removeEndSlash(parentPath) + SLASH;
        if (!childPath.startsWith(parentPath)) return false;
        String relativePath = childPath.substring(parentPath.length());
        if (relativePath.startsWith(SLASH)) relativePath = relativePath.substring(1);
        if (recursive) {
            return !relativePath.isEmpty();
        } else {
            return !relativePath.isEmpty() && !relativePath.contains(SLASH);
        }
    }
}

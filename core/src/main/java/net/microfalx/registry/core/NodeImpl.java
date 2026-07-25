package net.microfalx.registry.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.microfalx.registry.Node;
import net.microfalx.registry.RegistryService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Optional.ofNullable;
import static net.microfalx.lang.ArgumentUtils.requireNonNull;
import static net.microfalx.lang.StringUtils.defaultIfEmpty;
import static net.microfalx.lang.UriUtils.SLASH;

@Getter
@Setter(AccessLevel.PROTECTED)
@ToString
final class NodeImpl implements Node {

    private final Node parent;
    private final String path;

    private volatile boolean exists;
    private volatile boolean leaf;
    private final AtomicInteger updateCount = new AtomicInteger();
    private final AtomicInteger internalVersion = new AtomicInteger(0);

    private volatile LocalDateTime createdAt;
    private volatile LocalDateTime updatedAt;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    volatile byte[] data;

    RegistryImpl registry;

    NodeImpl(Node node) {
        requireNonNull(node);
        this.parent = null;
        this.path = node.getPath();
        this.exists = node.exists();
        this.leaf = node.isLeaf();
        this.updateCount.set(node.getUpdateCount());
        this.internalVersion.set(node.getVersion());
        this.createdAt = node.getCreatedAt();
        this.updatedAt = node.getUpdatedAt();
    }

    NodeImpl(Node parent, String path) {
        requireNonNull(path);
        this.parent = parent;
        this.path = defaultIfEmpty(path, SLASH);
    }

    @Override
    public Optional<Node> getParent() {
        return ofNullable(parent);
    }

    @Override
    public boolean exists() {
        return exists;
    }

    @Override
    public int getVersion() {
        return Math.max(1, internalVersion.get());
    }

    public int getUpdateCount() {
        return updateCount.get();
    }

    void setVersion(int version) {
        internalVersion.set(version);
    }

    void incrementVersion() {
        internalVersion.incrementAndGet();
    }

    boolean setVersion(int currentVersion, int nextVersion) {
        if (internalVersion.get() == 0) {
            return internalVersion.compareAndSet(0, nextVersion);
        } else {
            return internalVersion.compareAndSet(currentVersion, nextVersion);
        }
    }

    void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        this.updateCount.incrementAndGet();
    }

    RegistryImpl getRegistry() {
        if (registry == null) registry = (RegistryImpl) RegistryService.getInstance().getRegistry();
        return registry;
    }
}

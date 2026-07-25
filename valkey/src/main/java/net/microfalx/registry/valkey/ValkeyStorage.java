package net.microfalx.registry.valkey;

import net.microfalx.lang.annotation.Provider;
import net.microfalx.registry.Node;
import net.microfalx.registry.core.AbstractStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Provider
public class ValkeyStorage extends AbstractStorage {

    @Override
    public Collection<Node> getChildren(String path, boolean recursive) {
        return List.of();
    }

    @Override
    public Optional<Node> getNode(String path) {
        return Optional.empty();
    }

    @Override
    public boolean exists(String path) {
        return false;
    }

    @Override
    public byte[] get(String path) {
        return new byte[0];
    }

    @Override
    public void put(String path, byte[] data) {

    }

    @Override
    public void put(String path, byte[] data, int version) {

    }

    @Override
    public void remove(String path) {

    }
}

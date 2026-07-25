package net.microfalx.registry.valkey;

import net.microfalx.registry.Registry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class ValkeyStorageTest {

    private ValkeyStorage storage;

    @BeforeEach
    void setUp() {
        storage = new ValkeyStorage();
    }

    @Test
    void discovery() {
        assertSame(ValkeyStorage.class, Registry.get().getStorage().getClass());
    }

    @Test
    void initialize() {

    }

}
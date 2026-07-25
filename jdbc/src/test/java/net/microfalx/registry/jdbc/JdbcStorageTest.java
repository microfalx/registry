package net.microfalx.registry.jdbc;

import net.microfalx.registry.Registry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class JdbcStorageTest {

    private JdbcStorage storage;

    @BeforeEach
    void setUp() {
        storage = new JdbcStorage();
    }

    @Test
    void discovery() {
        assertSame(JdbcStorage.class, Registry.get().getStorage().getClass());
    }

    @Test
    void initialize() {

    }

}
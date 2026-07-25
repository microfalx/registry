package net.microfalx.registry.core;

import net.microfalx.registry.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RegistryImplTest {

    private Registry registry;

    @BeforeEach
    void setup() {
        List<Storage> storages = List.of(new MemoryStorage(), Mockito.mock(Storage.class));
        RegistryServiceImpl registryService = new RegistryServiceImpl(storages);
        registryService.initialize();
        registry = registryService.getRegistry();
    }

    @Test
    void existsReturnsFalseForNonExistentPath() {
        assertFalse(registry.exists("/nonexistent"));
    }

    @Test
    void existsReturnsTrueForExistingPath() {
        Data data = new DataImpl("/existing");
        data.set("test");
        registry.set(data);
        assertTrue(registry.exists("/existing"));
    }

    @Test
    void getReturnsEmptyForNonExistentPath() {
        assertTrue(registry.get("/nonexistent").isEmpty());
    }

    @Test
    void testSetAndGet() {
        Data data = new DataImpl("/test");
        data.set("value");
        registry.set(data);
        Optional<Data> retrieved = registry.get("/test");
        assertTrue(retrieved.isPresent());
        assertEquals("value", retrieved.get().get());
    }

    @Test
    void getOrCreateCreatesNewData() {
        Data data = registry.getOrCreate("/new");
        assertNotNull(data);
        assertFalse(data.exists());
    }

    @Test
    void getOrCreateReturnsExisting() {
        Data original = new DataImpl("/existing");
        original.set("original");
        registry.set(original);
        Data retrieved = registry.getOrCreate("/existing");
        assertTrue(retrieved.exists());
        assertEquals("original", retrieved.get());
    }

    @Test
    void lookupReturnsEmptyForNonExistent() {
        assertTrue(registry.lookup("/nonexistent").isEmpty());
    }

    @Test
    void lookupReturnsNodeForExisting() {
        Data data = new DataImpl("/node");
        data.set("node");
        registry.set(data);
        Optional<Node> node = registry.lookup("/node");
        assertTrue(node.isPresent());
        assertEquals("/node", node.get().getPath());
    }

    @Test
    void listReturnsEmptyForNonExistentPath() {
        Iterable<Data> list = registry.list("/nonexistent");
        assertFalse(list.iterator().hasNext());
    }

    @Test
    void listReturnsDataForExistingPath() {
        Data data1 = new DataImpl("/parent/child1");
        data1.set("child1");
        registry.set(data1);
        Data data2 = new DataImpl("/parent/child2");
        data2.set("child2");
        registry.set(data2);
        Iterable<Data> list = registry.list("/parent");
        List<Data> dataList = new java.util.ArrayList<>();
        list.forEach(dataList::add);
        assertEquals(2, dataList.size());
    }

    @Test
    void walk() {
        Data data = new DataImpl("/walk");
        data.set("walk");
        registry.set(data);
        boolean[] visited = {false};
        registry.walk("/walk", 1, (path, node) -> {
            visited[0] = true;
            return true;
        });
        assertTrue(visited[0]);
    }

}
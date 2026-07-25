package net.microfalx.registry.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.microfalx.lang.ArgumentUtils;
import net.microfalx.lang.ClassUtils;
import net.microfalx.lang.Initializable;
import net.microfalx.lang.ObjectUtils;
import net.microfalx.lang.annotation.Provider;
import net.microfalx.registry.Registry;
import net.microfalx.registry.RegistryService;
import net.microfalx.registry.Serde;
import net.microfalx.registry.Storage;
import net.microfalx.threadpool.ThreadPool;
import net.microfalx.threadpool.Trigger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static net.microfalx.lang.ArgumentUtils.requireNonNull;
import static net.microfalx.lang.ClassUtils.resolveProviderInstances;

@Slf4j
@Provider
public class RegistryServiceImpl implements RegistryService, Initializable {

    private final List<Storage> storages;

    private volatile Storage storage;
    private volatile Serde serde;
    @Getter private Registry registry;

    public RegistryServiceImpl() {
        this(Collections.emptyList());
    }

    public RegistryServiceImpl(List<Storage> storages) {
        requireNonNull(storages);
        this.storages = new ArrayList<>(storages);
    }

    public List<Storage> getStorages() {
        return unmodifiableList(storages);
    }

    public Storage getStorage() {
        if (storage == null) selectStorage();
        return storage;
    }

    @Override
    public Serde getSerde() {
        return serde;
    }

    @Override
    public void initialize(Object... context) {
        selectStorage();
        selectSerde();
        registry = new RegistryImpl(this);
    }

    @Override
    public void start() {
        ThreadPool.get().schedule(this::maintenance, Trigger.fixedDelay(Duration.ofSeconds(60)));
    }

    private synchronized void selectStorage() {
        if (storage != null) return;
        List<Storage> finalStorages = storages;
        if (finalStorages.isEmpty()) finalStorages.addAll(resolveProviderInstances(Storage.class));
        for (Storage currentStorage : finalStorages) {
            if (!currentStorage.isEnabled()) {
                LOGGER.info("Registry storage {} is disabled, skip it", ClassUtils.getName(currentStorage));
                continue;
            }
            storage = currentStorage;
            break;
        }
        LOGGER.info("Use registry storage {}", ClassUtils.getName(storage));
    }

    private synchronized void selectSerde() {
        Collection<Serde> serdes = resolveProviderInstances(Serde.class);
        if (serdes.isEmpty()) {
            throw new IllegalStateException("No serialization/deserialization providers found");
        }
        serde = serdes.iterator().next();
        if (serde instanceof Initializable) {
            ((Initializable) serde).initialize();
        }
        LOGGER.info("Use registry serde {}", ClassUtils.getName(serde));
    }

    private void maintenance() {

    }
}

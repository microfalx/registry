package net.microfalx.registry;

import net.microfalx.lang.service.Service;

import java.util.List;

/**
 * A service which manages the registry and registry storages.
 */
public interface RegistryService extends Service {

    static RegistryService getInstance() {
        return Service.lookup(RegistryService.class);
    }

    /**
     * Returns the current registry.
     *
     * @return a non-null instance
     */
    Registry getRegistry();

    /**
     * Returns the registered storages.
     *
     * @return a non-null instance
     */
    List<Storage> getStorages();

    /**
     * Returns the current storage.
     *
     * @return a non-null instance
     */
    Storage getStorage();

    /**
     * Returns the serialization interface.
     *
     * @return a non-null instance
     */
    Serde getSerde();
}

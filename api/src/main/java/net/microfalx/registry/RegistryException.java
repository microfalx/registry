package net.microfalx.registry;

import net.microfalx.lang.service.ServiceException;

/**
 * Base exception for all registry failures.
 */
public class RegistryException extends ServiceException {

    public RegistryException(String message) {
        super(message);
    }

    public RegistryException(String message, Throwable cause) {
        super(message, cause);
    }
}

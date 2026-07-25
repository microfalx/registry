package net.microfalx.registry;

/**
 * An exception raised when entries are versioned and there is a conflice
 */
public class RegistryVersionConflictException extends RegistryException {

    public RegistryVersionConflictException(String message) {
        super(message);
    }

    public RegistryVersionConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

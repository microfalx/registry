# Registry

A library for managing data as a hierarchical registry.

The registry models data as a tree of nodes with parent/child relationships. Each node can store a primary value plus additional key/value attributes, making it easy to organize structured data by path.

Registry updates are versioned and support compare-and-swap (CAS) semantics, so concurrent changes can be detected and handled safely.

Storage is abstracted behind pluggable registry backends, which allows custom implementations on top of different persistence technologies. The project includes support for in-memory storage, database-backed registries, and key/value stores such as Valkey.

## Examples

Store and retrive data from the registry:

```java
import net.microfalx.registry.Data;
import net.microfalx.registry.Registry;

public class RegistryExample {

	public void updateApp() {
		Registry registry = Registry.get();

        // store data
		Data app = registry.getOrCreate("/app");
		app.set("registry");
		app.setAttribute("enabled", true);
		app.setAttribute("port", 8080);

		registry.set(app);

        // retrieve data
		registry.get("/app").ifPresent(data -> {
			String name = data.get();
			boolean enabled = data.getAttribute("enabled", false);
			int port = data.getAttribute("port", 0);
		});
	}
}
```
List existing services in the registry:

```java
import net.microfalx.registry.Data;
import net.microfalx.registry.Registry;

public class RegistryListExample {

	public void listServices() {
		Registry registry = Registry.get();
        
		for (Data data : registry.list("/services")) {
			String path = data.getNode().getPath();
			boolean exists = data.exists();
			String value = data.get();
		}
	}
}
```


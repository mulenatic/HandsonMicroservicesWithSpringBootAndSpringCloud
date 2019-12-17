import java.time.LocalDateTime;

/**
 * Event
 */
public class Event<K, T> {

    public enum Type {
	CREATE, DELETE
    };

    private final Event.Type eventType;
    private final K key;
    private final T data;
    private final LocalDateTime evenetCreatedAt;

	public Event() {
		this.eventType = eventType;
		this.key = key;
		this.data = data;
		this.evenetCreatedAt = evenetCreatedAt;
	}

    public Event(Type eventType, K key, T data, LocalDateTime evenetCreatedAt) {
		this.eventType = eventType;
		this.key = key;
		this.data = data;
		this.evenetCreatedAt = evenetCreatedAt;
	}

    
    

    
    
}

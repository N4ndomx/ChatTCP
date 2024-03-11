package client.observer;

public interface EventListener {
    void update(String eventType, String msg, String nameUser);
}

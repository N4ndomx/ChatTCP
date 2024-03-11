package client.observer;

public class MensajeRecibido {
    public EventManager events;

    public MensajeRecibido() {
        events = new EventManager("privados", "general");
    }

    public void recepcionMsg(String type, String msgRec, String nameUser) {
        if (type.equals("P")) {
            events.notify("privados", msgRec, nameUser);
        } else {
            events.notify("general", msgRec, nameUser);
        }

    }

}

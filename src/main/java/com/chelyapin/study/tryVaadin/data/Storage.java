package com.chelyapin.study.tryVaadin.data;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class Storage {
    @Getter
    private Queue<Message> messages = new ConcurrentLinkedQueue<>();
    private ComponentEventBus eventBus = new ComponentEventBus(new Div());

    public void addRecord(String username, String textOfMessage) {
        messages.add(new Message(username, textOfMessage));
        eventBus.fireEvent(new ChatEvent());
    }

    public int size() {
        return messages.size();
    }

    public void addRecordJoined(String user) {
        messages.add(new Message("", user));
        eventBus.fireEvent(new ChatEvent());
    }

    public static class ChatEvent extends ComponentEvent<Div> {

        public ChatEvent() {
            super(new Div(), false);
        }
    }

    public Registration attachEventListener(ComponentEventListener<ChatEvent> messageListener) {
        return eventBus.addListener(ChatEvent.class, messageListener);
    }

    @Getter
    @AllArgsConstructor
    public static class Message {
        private String username;
        private String messageBody;
    }
}

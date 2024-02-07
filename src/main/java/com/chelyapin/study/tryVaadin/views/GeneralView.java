package com.chelyapin.study.tryVaadin.views;

import com.chelyapin.study.tryVaadin.data.Storage;
import com.github.rjeschke.txtmark.Processor;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class GeneralView extends VerticalLayout {
    private final Storage storage;
    private Registration registration;
    private VerticalLayout chat;
    private VerticalLayout login;
    private Grid<Storage.Message> messageGrid;
    private String user;

    @Autowired
    public GeneralView(Storage storage) {
        setSizeFull();
        this.storage = storage;
        this.login = buildLogin();
        this.chat = buildChat();
        chat.setSizeFull();
        this.add(login);
        this.add(chat);
    }

    private VerticalLayout buildLogin() {
        VerticalLayout loginLayout = new VerticalLayout() {{
            TextField loginField = new TextField();
            loginField.setPlaceholder("Write your nickname");
            add(
                    loginField,
                    new Button("Login") {{
                        addClickListener(click -> {
                            String username = loginField.getValue();
                            if (username.isEmpty()) {
                                return;
                            }
                            login.setVisible(false);
                            chat.setVisible(true);
                            user = username;
                            storage.addRecordJoined(user);
                        });
                        addClickShortcut(Key.ENTER);
                    }}
            );
        }};
        loginLayout.setSizeFull();
        loginLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        loginLayout.setAlignItems(Alignment.CENTER);
        return loginLayout;
    }

    private VerticalLayout buildChat() {
        messageGrid = new Grid<>();
        VerticalLayout chatLayout = new VerticalLayout();
        chatLayout.setVisible(false);
        messageGrid.setItems(storage.getMessages());
        messageGrid.addColumn(new ComponentRenderer<>(message -> new Html(getRightViewOfMessage(message))))
                .setAutoWidth(true);
        messageGrid.addClassName("chatGrid");
        HorizontalLayout layoutForHeader = new HorizontalLayout();
        layoutForHeader.setWidthFull();
        layoutForHeader.setJustifyContentMode(JustifyContentMode.CENTER);
        Div header =  new Div("Chat");
        header.getStyle().set("font-size", "30px");
        layoutForHeader.add(header);

        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setPlaceholder("Write your message");
        Button button = new Button("Sent message") {{
            addClickListener(click -> {
                String textOfMessage = textField.getValue();
                if (textOfMessage.isEmpty()) {
                    return;
                }
                storage.addRecord(user, textOfMessage);
                textField.clear();
            });
            addClickShortcut(Key.ENTER);
        }};
        HorizontalLayout sentMessage = new HorizontalLayout();
        sentMessage.setWidthFull();
        sentMessage.add(textField, button);
        chatLayout.add(
                layoutForHeader,
                messageGrid,
                sentMessage
        );
        messageGrid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
        messageGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        return chatLayout;
    }

    private String getRightViewOfMessage(Storage.Message message) {
        if (message.getUsername().isEmpty()) {
            return Processor.process(String.format("User **%s** joined chat", message.getMessageBody()));
        } else {
            return Processor.process(String.format("**%s**: %s", message.getUsername(), message.getMessageBody()));
        }
    }

    public void onMessage(Storage.ChatEvent event) {
        if (getUI().isEmpty()) {
            return;
        }

        UI ui = getUI().get();
        ui.getSession().lock();
        ui.beforeClientResponse(messageGrid, ctx -> messageGrid.scrollToEnd());
        ui.access(() -> messageGrid.getDataProvider().refreshAll());
        ui.getSession().unlock();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        registration = storage.attachEventListener(this::onMessage);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        registration.remove();
    }
}

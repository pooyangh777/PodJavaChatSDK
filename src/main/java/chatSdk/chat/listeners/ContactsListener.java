package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.ChatResponse2;
import chatSdk.dataTransferObject.contacts.inPut.*;

public interface ContactsListener {
    default void onGetContacts(String content, ChatResponse2<ResultContact> response) {}
    default void onGetContacts2(ChatResponse<Contact[]> contacts) {}

    default void onContactAdded(String content, ChatResponse2<ResultAddContact> response) {}
    default void onRemoveContact(String content, ChatResponse2<ResultRemoveContact> response) {}
    default void onUpdateContact(String content, ChatResponse2<ResultUpdateContact> response) {}
    default void onSyncContact(String content, ChatResponse2<Contacts> chatResponse) {}
    default void onSearchContact(String content, ChatResponse2<ResultContact> response) {}
}

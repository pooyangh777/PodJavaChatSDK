package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.contacts.inPut.ResultAddContact;
import chatSdk.dataTransferObject.contacts.inPut.ResultContact;
import chatSdk.dataTransferObject.contacts.inPut.ResultRemoveContact;
import chatSdk.dataTransferObject.contacts.inPut.ResultUpdateContact;

public interface ContactsListener {
    default void onGetContacts(String content, ChatResponse<ResultContact> response) {}
    default void onContactAdded(String content, ChatResponse<ResultAddContact> response) {}
    default void onRemoveContact(String content, ChatResponse<ResultRemoveContact> response) {}
    default void onUpdateContact(String content, ChatResponse<ResultUpdateContact> response) {}
    default void onSyncContact(String content, ChatResponse<chatSdk.dataTransferObject.contacts.inPut.Contacts> chatResponse) {}
    default void onSearchContact(String content, ChatResponse<ResultContact> response) {}
}

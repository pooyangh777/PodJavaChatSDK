package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.contacts.inPut.ResultContact;

public interface Contacts
{
    default void onGetContacts(String content, ChatResponse<ResultContact> response) {
    }
}

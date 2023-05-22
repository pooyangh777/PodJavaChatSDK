package chatSdk.chat.chatInterface;

import chatSdk.dataTransferObject.contacts.outPut.GetContactsRequest;

public interface ContactsInterface {
    String getContacts(GetContactsRequest request);
}

package chatSdk.dataTransferObject.chat;

import chatSdk.dataTransferObject.ChatResponse2;
import chatSdk.dataTransferObject.contacts.inPut.Contacts;
import chatSdk.dataTransferObject.contacts.inPut.ResultAddContact;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
//import org.springframework.beans.BeanUtils;
import chatSdk.dataTransferObject.contacts.inPut.Contact;
import chatSdk.dataTransferObject.user.inPut.LinkedUser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Util {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

//    public static ChatResponse2<ResultAddContact> getReformatOutPutAddContact(Contacts contacts, String uniqueId) {
//        ChatResponse2<ResultAddContact> chatResponse = new ChatResponse2<>();
//        chatResponse.setUniqueId(uniqueId);
//
//        ResultAddContact resultAddContact = new ResultAddContact();
//        resultAddContact.setContentCount(1);
//        Contact contact = new Contact();
//
//        contact.setCellphoneNumber(contacts.getResult().get(0).getCellphoneNumber());
//        contact.setEmail(contacts.getResult().get(0).getEmail());
//        contact.setFirstName(contacts.getResult().get(0).getFirstName());
//        contact.setId(contacts.getResult().get(0).getId());
//        contact.setLastName(contacts.getResult().get(0).getLastName());
//        contact.setUniqueId(contacts.getResult().get(0).getUniqueId());
//
//        LinkedUser linkedUser = new LinkedUser();
//        BeanUtils.copyProperties(contacts.getResult().get(0).getLinkedUser(), linkedUser);
//        linkedUser.setCoreUserId(contacts.getResult().get(0).getLinkedUser().getId());
//        linkedUser.setId(0);
//
//        contact.setLinkedUser(linkedUser);
//
//        resultAddContact.setContact(contact);
//
//        chatResponse.setResult(resultAddContact);
//
//        return chatResponse;
//    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static <T extends Number> boolean isNullOrEmpty(ArrayList<T> list) {
        return list == null || list.size() == 0;
    }

    public static <T> boolean isNullOrEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T extends Number> boolean isNullOrEmpty(T number) {
        String num = String.valueOf(number);
        return number == null || num.equals("0");
    }

    public static <T extends Object> boolean isNullOrEmpty(List<T> list) {
        return list == null || list.size() == 0;
    }


    public static <T extends Number> boolean isNullOrEmptyNumber(List<T> list) {
        return list == null || list.size() == 0;
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static <T> String listToJson(ArrayList<T> list, Gson gson) {
        Type listType = new TypeToken<List<T>>() {
        }.getType();

        return gson.toJson(list, listType);
    }

    public static <T> JsonArray listToJsonArray(ArrayList<T> list, Gson gson) {

        JsonElement element = gson.toJsonTree(list, new TypeToken<List<T>>() {
        }.getType());

        return element.getAsJsonArray();
    }

    public static <T extends List> List<T> JsonToList(String json, Gson gson) {

        return gson.fromJson(json, new TypeToken<List<T>>() {
        }.getType());
    }
}

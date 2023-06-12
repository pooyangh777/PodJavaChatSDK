package chatSdk.dataTransferObject.chat;

public enum ChatMessageType {
    CREATE_THREAD(1),
    MESSAGE(2),
    SENT(3),
    DELIVERY(4),
    SEEN(5),
    PING(6),
    BLOCK(7),
    UNBLOCK(8),
    LEAVE_THREAD(9),
    ADD_PARTICIPANT(11),
    GET_STATUS(12),
    GET_CONTACTS(13),
    GET_THREADS(14),
    GET_HISTORY(15),
    CHANGE_TYPE(16),
    REMOVED_FROM_THREAD(17),
    REMOVE_PARTICIPANT(18),
    MUTE_THREAD(19),
    UNMUTE_THREAD(20),
    UPDATE_THREAD_INFO(21),
    FORWARD_MESSAGE(22),
    USER_INFO(23),
    USER_STATUS(24),
    GET_BLOCKED(25),
    RELATION_INFO(26),
    THREAD_PARTICIPANTS(27),
    EDIT_MESSAGE(28),
    DELETE_MESSAGE(29),
    LAST_SEEN_UPDATED(31),
    GET_MESSAGE_DELIVERY_PARTICIPANTS(32),
    GET_MESSAGE_SEEN_PARTICIPANTS(33),
    IS_NAME_AVAILABLE(34),
    JOIN_THREAD(39),
    BOT_MESSAGE(40),
    SPAM_PV_THREAD(41),
    SET_ROLE_TO_USER(42),
    REMOVE_ROLE_FROM_USER(43),
    CLEAR_HISTORY(44),
    SYSTEM_MESSAGE(46),
    GET_NOT_SEEN_DURATION(47),
    PIN_THREAD(48),
    UNPIN_THREAD(49),
    PIN_MESSAGE(50),
    UNPIN_MESSAGE(51),
    UPDATE_CHAT_PROFILE(52),
    CHANGE_THREAD_PRIVACY(53),
    GET_PARTICIPANT_ROLES(54),
    GET_REPORT_REASONS(56),
    REPORT_THREAD(57),
    REPORT_USER(58),
    REPORT_MESSAGE(59),
    GET_CONTACT_NOT_SEEN_DURATION(60),
    ALL_UNREAD_MESSAGE_COUNT(61),
    CREATE_BOT(62),
    DEFINE_BOT_COMMAND(63),
    START_BOT(64),
    STOP_BOT(65),
    LAST_MESSAGE_DELETED(66),
    LAST_MESSAGE_EDITED(67),
    BOT_COMMANDS(68),
    THREAD_ALL_BOTS(69),
    CALL_REQUEST(70),
    ACCEPT_CALL(71),
    REJECT_CALL(72),
    RECEIVE_CALL_REQUEST(73),
    START_CALL(74),
    END_CALL_REQUEST(75),
    END_CALL(76),
    GET_CALLS(77),
    RECONNECT(78),
    CONNECT(79),
    CONTACT_SYNCED(90),
    GROUP_CALL_REQUEST(91),
    LEAVE_CALL(92),
    ADD_CALL_PARTICIPANT(93),
    CALL_PARTICIPANT_JOINED(94),
    REMOVE_CALL_PARTICIPANT(95),
    TERMINATE_CALL(96),
    MUTE_CALL_PARTICIPANT(97),
    UNMUTE_CALL_PARTICIPANT(98),
    CANCEL_GROUP_CALL(99),
    LOGOUT(100),
    LOCATION_PING(101),
    CLOSE_THREAD(102),
    REMOVE_BOT_COMMANDS(104),
    SEARCH(105),
    CONTINUE_SEARCH(106),
    REGISTER_ASSISTANT(107),
    DEACTIVATE_ASSISTANT(108),
    GET_ASSISTANTS(109),
    ACTIVE_CALL_PARTICIPANTS(110),
    CALL_SESSION_CREATED(111),
    IS_BOT_NAME_AVAILABLE(112),
    TURN_ON_VIDEO_CALL(113),
    TURN_OFF_VIDEO_CALL(114),
    ASSISTANT_HISTORY(115),
    BLOCK_ASSISTANT(116),
    UNBLOCK_ASSISTANT(117),
    BLOCKED_ASSISTANTS(118),
    RECORD_CALL(121),
    END_RECORD_CALL(122),
    START_SCREEN_SHARE(123),
    END_SCREEN_SHARE(124),
    DELETE_FROM_CALL_HISTORY(125),
    DESTINED_RECORD_CALL(126),
    GET_CALLS_TO_JOIN(129),
    MUTUAL_GROUPS(130),
    CREATE_TAG(140),
    EDIT_TAG(141),
    DELETE_TAG(142),
    ADD_TAG_PARTICIPANT(143),
    REMOVE_TAG_PARTICIPANT(144),
    GET_TAG_LIST(145),
    DELETE_MESSAGE_THREAD(151),
    EXPORT_CHAT(152),
    ADD_CONTACTS(200),
    REMOVE_CONTACTS(201),
    CONTACT_THREAD_UPDATE(220),
    SWITCH_TO_GROUP_CALL_REQUEST(221),
    RECORD_CALL_STARTED(222),
    ARCHIVE_THREAD(223),
    UNARCHIVE_THREAD(224),
    CALL_STICKER_SYSTEM_MESSAGE(225),
    CUSTOMER_INFO(226),
    RECALL_THREAD_PARTICIPANT(227),
    INQUIRY_CALL(228),
    CALL_RECORDING_FAILED(230),
    ERROR(999);

    private final int value;

    ChatMessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ChatMessageType check(int type) {
        switch (type) {
            case 1:
                return CREATE_THREAD;
            case 2:
                return MESSAGE;
            case 3:
                return SENT;
            case 4:
                return DELIVERY;
            case 5:
                return SEEN;
            case 6:
                return PING;
            case 7:
                return BLOCK;
            case 8:
                return UNBLOCK;
            case 9:
                return LEAVE_THREAD;
            case 11:
                return ADD_PARTICIPANT;
            case 12:
                return GET_STATUS;
            case 13:
                return GET_CONTACTS;
            case 14:
                return GET_THREADS;
            case 15:
                return GET_HISTORY;
            case 16:
                return CHANGE_TYPE;
            case 17:
                return REMOVED_FROM_THREAD;
            case 18:
                return REMOVE_PARTICIPANT;
            case 19:
                return MUTE_THREAD;
            case 20:
                return UNMUTE_THREAD;
            case 21:
                return UPDATE_THREAD_INFO;
            case 22:
                return FORWARD_MESSAGE;
            case 23:
                return USER_INFO;
            case 24:
                return USER_STATUS;
            case 25:
                return GET_BLOCKED;
            case 26:
                return RELATION_INFO;
            case 27:
                return THREAD_PARTICIPANTS;
            case 28:
                return EDIT_MESSAGE;
            case 29:
                return DELETE_MESSAGE;
            case 31:
                return LAST_SEEN_UPDATED;
            case 32:
                return GET_MESSAGE_DELIVERY_PARTICIPANTS;
            case 33:
                return GET_MESSAGE_SEEN_PARTICIPANTS;
            case 34:
                return IS_NAME_AVAILABLE;
            case 39:
                return JOIN_THREAD;
            case 40:
                return BOT_MESSAGE;
            case 41:
                return SPAM_PV_THREAD;
            case 42:
                return SET_ROLE_TO_USER;
            case 43:
                return REMOVE_ROLE_FROM_USER;
            case 44:
                return CLEAR_HISTORY;
            case 46:
                return SYSTEM_MESSAGE;
            case 47:
                return GET_NOT_SEEN_DURATION;
            case 48:
                return PIN_THREAD;
            case 49:
                return UNPIN_THREAD;
            case 50:
                return PIN_MESSAGE;
            case 51:
                return UNPIN_MESSAGE;
            case 52:
                return UPDATE_CHAT_PROFILE;
            case 53:
                return CHANGE_THREAD_PRIVACY;
            case 54:
                return GET_PARTICIPANT_ROLES;
            case 56:
                return GET_REPORT_REASONS;
            case 57:
                return REPORT_THREAD;
            case 58:
                return REPORT_USER;
            case 59:
                return REPORT_MESSAGE;
            case 60:
                return GET_CONTACT_NOT_SEEN_DURATION;
            case 61:
                return ALL_UNREAD_MESSAGE_COUNT;
            case 62:
                return CREATE_BOT;
            case 63:
                return DEFINE_BOT_COMMAND;
            case 64:
                return START_BOT;
            case 65:
                return STOP_BOT;
            case 66:
                return LAST_MESSAGE_DELETED;
            case 67:
                return LAST_MESSAGE_EDITED;
            case 68:
                return BOT_COMMANDS;
            case 69:
                return THREAD_ALL_BOTS;
            case 70:
                return CALL_REQUEST;
            case 71:
                return ACCEPT_CALL;
            case 72:
                return REJECT_CALL;
            case 73:
                return RECEIVE_CALL_REQUEST;
            case 74:
                return START_CALL;
            case 75:
                return END_CALL_REQUEST;
            case 76:
                return END_CALL;
            case 77:
                return GET_CALLS;
            case 78:
                return RECONNECT;
            case 79:
                return CONNECT;
            case 90:
                return CONTACT_SYNCED;
            case 91:
                return GROUP_CALL_REQUEST;
            case 92:
                return LEAVE_CALL;
            case 93:
                return ADD_CALL_PARTICIPANT;
            case 94:
                return CALL_PARTICIPANT_JOINED;
            case 95:
                return REMOVE_CALL_PARTICIPANT;
            case 96:
                return TERMINATE_CALL;
            case 97:
                return MUTE_CALL_PARTICIPANT;
            case 98:
                return UNMUTE_CALL_PARTICIPANT;
            case 99:
                return CANCEL_GROUP_CALL;
            case 100:
                return LOGOUT;
            case 101:
                return LOCATION_PING;
            case 102:
                return CLOSE_THREAD;
            case 104:
                return REMOVE_BOT_COMMANDS;
            case 105:
                return SEARCH;
            case 106:
                return CONTINUE_SEARCH;
            case 107:
                return REGISTER_ASSISTANT;
            case 108:
                return DEACTIVATE_ASSISTANT;
            case 109:
                return GET_ASSISTANTS;
            case 110:
                return ACTIVE_CALL_PARTICIPANTS;
            case 111:
                return CALL_SESSION_CREATED;
            case 112:
                return IS_BOT_NAME_AVAILABLE;
            case 113:
                return TURN_ON_VIDEO_CALL;
            case 114:
                return TURN_OFF_VIDEO_CALL;
            case 115:
                return ASSISTANT_HISTORY;
            case 116:
                return BLOCK_ASSISTANT;
            case 117:
                return UNBLOCK_ASSISTANT;
            case 118:
                return BLOCKED_ASSISTANTS;
            case 121:
                return RECORD_CALL;
            case 122:
                return END_RECORD_CALL;
            case 123:
                return START_SCREEN_SHARE;
            case 124:
                return END_SCREEN_SHARE;
            case 125:
                return DELETE_FROM_CALL_HISTORY;
            case 126:
                return DESTINED_RECORD_CALL;
            case 129:
                return GET_CALLS_TO_JOIN;
            case 130:
                return MUTUAL_GROUPS;
            case 140:
                return CREATE_TAG;
            case 141:
                return EDIT_TAG;
            case 142:
                return DELETE_TAG;
            case 143:
                return ADD_TAG_PARTICIPANT;
            case 144:
                return REMOVE_TAG_PARTICIPANT;
            case 145:
                return GET_TAG_LIST;
            case 151:
                return DELETE_MESSAGE_THREAD;
            case 152:
                return EXPORT_CHAT;
            case 200:
                return ADD_CONTACTS;
            case 201:
                return REMOVE_CONTACTS;
            case 220:
                return CONTACT_THREAD_UPDATE;
            case 221:
                return SWITCH_TO_GROUP_CALL_REQUEST;
            case 222:
                return RECORD_CALL_STARTED;
            case 223:
                return ARCHIVE_THREAD;
            case 224:
                return UNARCHIVE_THREAD;
            case 225:
                return CALL_STICKER_SYSTEM_MESSAGE;
            case 226:
                return CUSTOMER_INFO;
            case 227:
                return RECALL_THREAD_PARTICIPANT;
            case 228:
                return INQUIRY_CALL;
            case 230:
                return CALL_RECORDING_FAILED;
        }
        return null;
    }
}
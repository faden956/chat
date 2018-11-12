package server;

public enum MessageType {
     NAME_REQUEST,
     USER_NAME,
    NAME_ACCEPTED,
    TEXT,
    USER_ADDED,
    USER_REMOVED;
}
// NAME_REQUEST - запрос имени.
// USER_NAME - имя пользователя.
// NAME_ACCEPTED - имя принято.
// TEXT - текстовое сообщение.
// USER_ADDED - пользователь добавлен.
// USER_REMOVED - пользователь удален.
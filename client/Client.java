package client;


import server.Connection;
import server.ConsoleHelper;
import server.Message;
import server.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client {

    protected Connection connection;
    private volatile boolean clientConnected = false;

    protected String getServerAddress(){ // пользователь вводит ip
            String ip = ConsoleHelper.readString();
            return ip;
    }

    protected int getServerPort() { // порт
        int port = ConsoleHelper.readInt();
        return port;
    }

    protected String getUserName(){ // имя
        String name = ConsoleHelper.readString();
        return name;
    }

    protected boolean shouldSendTextFromConsole(){ // флаг на то надо ли отправлять текст в консоль
        return true;
    }

    protected SocketThread getSocketThread(){
        SocketThread socketThread = new SocketThread();
        return socketThread;
    }

    protected void sendTextMessage(String text){ // формируется сообщение и отправляется через соединение
        try {
            Message message = new Message(MessageType.TEXT, text);
            connection.send(message);
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Не удалось отправить сообщение");
            clientConnected = false;
        }
    }

    public void run(){
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        try {
            synchronized(this) { wait();}
        }
        catch(InterruptedException e) {
            ConsoleHelper.writeMessage("Произошла непоправимая ошибка.");
            System.exit(1);
        }
        if(clientConnected){
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");
        } else {
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        }
        while (clientConnected){
            String text = ConsoleHelper.readString();
            if(text.equals("exit")){
                break;
            }
            if(shouldSendTextFromConsole()){
                sendTextMessage(text);
            }
        }
    }

    public class SocketThread extends  Thread{ // устанавливает соединение с сервром и принимает сообщения

        protected void processIncomingMessage(String message){ // выводим сообщение
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName){ // выводим что юзер подключился
            ConsoleHelper.writeMessage(userName + " подключился.");
        }

        protected void informAboutDeletingNewUser(String userName){ // выводим что юзер вышел
            ConsoleHelper.writeMessage(userName + " вышел из чата.");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected){ // устанавливаем подключился ли клиент и пробуждаем его нить
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this){
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException{ // принимает имя и запускает пробуждение нити
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.NAME_REQUEST) {
                    String name = getUserName();
                    Message nameMessage = new Message(MessageType.USER_NAME, name);
                    connection.send(nameMessage);
                }
                if (message.getType() == MessageType.NAME_ACCEPTED) {
                    notifyConnectionStatusChanged(true);
                    return;
                }
                if (message.getType() != MessageType.NAME_ACCEPTED && message.getType() != MessageType.NAME_REQUEST) {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException{ // принимает сообщения и в зависимоссти от типа выводит в нужном месте инфу
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    processIncomingMessage(message.getData());
                }
                if (message.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(message.getData());
                }
                 if (message.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(message.getData());
                }
                if(message.getType() != MessageType.TEXT && message.getType() != MessageType.USER_ADDED && message.getType() != MessageType.USER_REMOVED)
                    throw new IOException("Unexpected MessageType");
            }
        }

        public void run(){
            String ip = getServerAddress();
            int port = getServerPort();
            try {
                Socket socket = new Socket(ip, port);
                connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }

        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

}

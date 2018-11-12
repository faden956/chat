package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// принимает сообщения и рассылает их остальным участникам чата
public class Server {

    private static Map<String, Connection> connectionMap = new  ConcurrentHashMap<>(); // 1 - имя клиента  2 -  соедениение с ним // содержит все соединения с сервером

    private static class Handler extends Thread { // реализует протокол общения с клиентом

        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException{ // сервер просит ввести логин  - клиент вводит логин  - клиент добавляется в список соеденений
            while (true){
                connection.send(new Message(MessageType.NAME_REQUEST,"Введите логин"));
                Message message = connection.receive();
                if(message.getType() == MessageType.USER_NAME && !message.getData().isEmpty() && !connectionMap.containsKey(message.getData())){
                    connectionMap.put(message.getData(),connection);
                    connection.send(new Message(MessageType.NAME_ACCEPTED, "Логин зарегестрирован"));
                    return message.getData();
                }
            }
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException{ // оправляем пользователю инфу об остальных соедениях
            for(Map.Entry<String, Connection> pair : connectionMap.entrySet()){
                if(!userName.equals(pair.getKey())) {
                    Message message = new Message(MessageType.USER_ADDED, pair.getKey());
                    connection.send(message);
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{ // в цикле проверяем есть ли сообщение если да то ковертируем его в стринг и рассылаем все пользователям
            while (true){
                Message message = connection.receive();
                if (message != null && message.getType() == MessageType.TEXT){
                    String text = userName + ": " + message.getType();
                    Message newMessage = new Message(MessageType.TEXT, text);
                    Server.sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + message.getData()));
                }
                else {
                    ConsoleHelper.writeMessage(" Неверный тип сообщения");
                }
            }
        }

        public void run(){ // выводим сообщение что установлено соединение с сокетом // запускакаем рукопожатие , оправку клиенту всех соединений , и проверку на новые сообщения
            ConsoleHelper.writeMessage(socket.getRemoteSocketAddress().toString());
            String name = null;
            Connection connection = null;
            try {
                connection = new Connection(socket);
                name = serverHandshake(connection);
                sendListOfUsers(connection, name);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, name));
                serverMainLoop(connection,name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (name != null){
                    connectionMap.remove(name);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED,name));
                }
            }
        }
    }

    public static void main(String[] args) throws IOException { // пользователь вводит порт. потом с помощью этого создается сокет для сервера с указанным портом  // в цикле сервер пытается принять сокеты и создать калсс хандел
        int port = ConsoleHelper.readInt();
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Сервер запущен.");

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Handler handler = new Handler(socket);
                handler.start();
            }
            catch (IOException e) {
                serverSocket.close();
                ConsoleHelper.writeMessage(" произошла ошибка.");
                break;
            }
        }
    }

    public static void sendBroadcastMessage(Message message){ // рассылает все сообщение вызывая перебирая все соединения
        try{
            for(Map.Entry<String, Connection> pair : connectionMap.entrySet()){
                 pair.getValue().send(message);
            }
        }
        catch (Exception e){
            ConsoleHelper.writeMessage("Не можем доставить сообщение всем пользователям");
        }
    }
}

package client;

public class ClientGuiController extends Client {
    private ClientGuiModel model = new ClientGuiModel();
    private ClientGuiView view = new ClientGuiView(this);

    protected SocketThread getSocketThread(){ //создаем сокет
        GuiSocketThread socketThread = new GuiSocketThread();
        return socketThread;
    }

    public void run(){{
        SocketThread socketThread = getSocketThread();
        socketThread.run();
    }}

    public String getServerAddress() { // запускает окно с ip
        return view.getServerAddress();
    }

    public int getServerPort(){ // окно с портом
        return view.getServerPort();
    }

    public String getUserName(){ // окно с именем
        return view.getUserName();
    }

    public ClientGuiModel getModel(){ // возвращаем модель
        return model;
    }

    public static void main(String[] args) { // запускаем контроллер
        ClientGuiController clientGuiController = new ClientGuiController();
        clientGuiController.run();
    }

    public class GuiSocketThread extends SocketThread { // так же как и сокет тред в клиенте  только вместо отправки в консоль отправляется в модель и простит виев обновить экран
        @Override
        protected void processIncomingMessage(String message){
            model.setNewMessage(message);
            view.refreshMessages();
        }

        @Override
        protected void informAboutAddingNewUser(String userName){
            model.addUser(userName);
            view.refreshUsers();
        }

        @Override
        protected void informAboutDeletingNewUser(String userName){
            model.deleteUser(userName);
            view.refreshUsers();
        }

        @Override
        protected void notifyConnectionStatusChanged(boolean clientConnected){
            view.notifyConnectionStatusChanged(clientConnected);
        }
    }
}

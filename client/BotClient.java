package client;

import server.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {

    @Override
    protected SocketThread getSocketThread(){
        BotSocketThread socketThread = new BotSocketThread();
        return socketThread;
    }

    @Override
    protected boolean shouldSendTextFromConsole(){
        return false;
    }

    @Override
    protected String getUserName(){
        String userName = "date_bot_"+(int) (Math.random()*100);
        return userName;
    }

    public class BotSocketThread extends SocketThread{

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            String[] data = message.split(": ");
            SimpleDateFormat dateFormat = null;
            if (data.length == 2) {
                String userQuestion = data[1];
                switch (userQuestion) {
                    case "дата":
                        dateFormat = new SimpleDateFormat("d.MM.YYYY");
                        break;
                    case "день":
                        dateFormat = new SimpleDateFormat("d");
                        break;
                    case "месяц":
                        dateFormat = new SimpleDateFormat("MMMM");
                        break;
                    case "год":
                        dateFormat = new SimpleDateFormat("YYYY");
                        break;
                    case "время":
                        dateFormat = new SimpleDateFormat("H:mm:ss");
                        break;
                    case "час":
                        dateFormat = new SimpleDateFormat("H");
                        break;
                    case "минуты":
                        dateFormat = new SimpleDateFormat("m");
                        break;
                    case "секунды":
                        dateFormat = new SimpleDateFormat("s");
                        break;
                }
            }
            if (dateFormat != null) {
                String dateString = dateFormat.format(Calendar.getInstance().getTime());
                String botAnswer = String.format("Информация для %s: %s", data[0], dateString);
                sendTextMessage(botAnswer);
            }
        }

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException{
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }
    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
}

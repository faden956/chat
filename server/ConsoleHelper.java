package server;
// вся работа с консолью происходит тут
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message){
        System.out.println(message);
    }

    public static String readString(){ // вводим текст
        while (true) {
            try {
                String text = bufferedReader.readLine();
                return text;
            } catch (IOException e) {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        }
    }

    public static int readInt() { // вводим число
        while (true) {
            try {
                int i = Integer.parseInt(readString());
                return i;
            } catch (NumberFormatException e) {
                System.out.println("Произошла ошибка\n" + "при попытке ввода числа. Попробуйте еще раз.");
            }
        }
    }
}
//В классе ConsoleHelper должно быть создано и инициализировано приватное, не финальное, статическое поле типа BufferedReader.
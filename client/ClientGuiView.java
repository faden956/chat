package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGuiView {
    private final ClientGuiController controller;

    private JFrame frame = new JFrame("Чат"); // создаем окно с заголовком
    private JTextField textField = new JTextField(50); // создаем Текстовое поле
    private JTextArea messages = new JTextArea(10, 50); // Тоже текствое поле только можно вводить несколько строк
    private JTextArea users = new JTextArea(10, 10);

    public ClientGuiView(ClientGuiController controller) { // записываем контроллер и запускаем создание оконг
        this.controller = controller;
        initView();
    }

    private void initView() { // говорим что нельзя писать в этих полях // добавляем в окно поля // говорим что выход на крестик // устанавливаем видимость
        textField.setEditable(false);
        messages.setEditable(false);
        users.setEditable(false);

        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messages), BorderLayout.WEST);
        frame.getContentPane().add(new JScrollPane(users), BorderLayout.EAST);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        textField.addActionListener(new ActionListener() {// отпраляет сообщение при нажатии кнопки также чистит поле
            public void actionPerformed(ActionEvent e) {
                controller.sendTextMessage(textField.getText());
                textField.setText("");
            }
        });
    }

    public String getServerAddress() { // открывает форму для ввода ip
        return JOptionPane.showInputDialog(frame, "Введите адрес сервера:", "Конфигурация клиента", JOptionPane.QUESTION_MESSAGE);
    }

    public int getServerPort() { // форма для ввода порта
        while (true) {
            String port = JOptionPane.showInputDialog(frame, "Введите порт сервера:", "Конфигурация клиента", JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Был введен некорректный порт сервера. Попробуйте еще раз.",
                        "Конфигурация клиента",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String getUserName() { // форма ввода имени
        return JOptionPane.showInputDialog(
                frame,
                "Введите ваше имя:",
                "Конфигурация клиента",
                JOptionPane.QUESTION_MESSAGE);
    }

    public void notifyConnectionStatusChanged(boolean clientConnected) { // устанавливаем соединение с сервером
        textField.setEditable(clientConnected);
        if (clientConnected) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Соединение с сервером установлено",
                    "Чат",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    frame,
                    "Клиент не подключен к серверу",
                    "Чат",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    public void refreshMessages() { // добовляем сообщения
        messages.append(controller.getModel().getNewMessage() + "\n");
    }

    public void refreshUsers() { // обновляем список юзеров
        ClientGuiModel model = controller.getModel();
        StringBuilder sb = new StringBuilder();
        for (String userName : model.getAllUserNames()) {
            sb.append(userName).append("\n");
        }
        users.setText(sb.toString());
    }
}

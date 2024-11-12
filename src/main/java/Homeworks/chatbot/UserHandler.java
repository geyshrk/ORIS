package Homeworks.chatbot;

import java.io.*;
import java.net.Socket;

public class UserHandler extends Thread {
    Socket userSocket;

    public UserHandler(Socket userSocket){
        this.userSocket = userSocket;
    }

    @Override
    public void run() {
        boolean flag = true;
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(userSocket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(userSocket.getInputStream()))){
            while (flag){
                String answer = reader.readLine();
                answer = answer == null ? null : answer.toLowerCase();
                if (answer == null || answer.contains("exit")) {
                    flag = false;
                    writer.write("exit");
                } else if (answer.contains("ошибк")) {
                    writer.write("Если оплата не прошла: 1) Повторите попытку через 20 минут, " +
                            "2) Обратитесь в банк, выпустивший карту, 3) Попробуйте оплатить другой картой.\n");
                } else if (answer.contains("оплат")) {
                    writer.write("Оплата производится при помощи одного из следующих средств: " +
                            "Банковская карта, баланс средств, подарочный сертификат Ozon, Система быстрых платежей, " +
                            "Баллы Ozon, Ozon Рассрочка, Ozon Карта, Наличные курьеру, Постоплата в пункте выдачи, SberPay\n");
                } else if (answer.contains("чек")) {
                    writer.write("Вы можете получить чеки двух типов: авансовый с информацией об оплате — " +
                            "в момент оплаты заказа; электронный — после получения товара.\n");
                } else if (answer.contains("документ")) {
                    writer.write("Если вы обращаетесь в поддержку Ozon с вопросами по оплате, у вас могут попросить документы из банка: " +
                            "выписка — документ о движении средств на счёте за определённый период, в котором указываются несколько операций, " +
                            "cправка — документ c данными по одной конкретной операции.\n");
                } else if (answer.contains("наличи")) {
                    writer.write("Если вы ввели название товара в поиск и не нашли его, проверьте ваш запрос. " +
                            "Если в запросе нет ошибки, но вы не нашли товар — его сейчас нет в наличии. " +
                            "Нажмите Похожие, чтобы найти аналогичные товары.\n");
                } else writer.write("Не удалось сформировать вопрос. Пожалуйста, смените формулировку\n");
                writer.flush();
            }

        } catch (IOException e){
            e.printStackTrace();
        }

    }
}

import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Bot extends TelegramLongPollingBot {
    private List<Case> listCases = new ArrayList<>();

    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    ObjectMapper objectMapper = new ObjectMapper();

    LocalDate today = LocalDate.now();
    String dayOfWeek = today.getDayOfWeek()
            .getDisplayName(TextStyle.SHORT, new Locale("ru"));

    String monday = "\n" +
            "08:30 – 09:50 Математика\n" +
            "10:00 – 11:20 Физика\n" +
            "11:30 – 12:50 Обеденный перерыв\n" +
            "13:00 – 14:20 Информатика\n" +
            "14:30 – 15:50 Английский язык\n" +
            "16:00 – 17:20 Физическая культура";
    String tuesday = "\n" +
            "08:30 – 09:50 История\n" +
            "10:00 – 11:20 Биология\n" +
            "11:30 – 12:50 Обеденный перерыв\n" +
            "13:00 – 14:20 Литература\n" +
            "14:30 – 15:50 Английский язык\n" +
            "16:00 – 17:20 Творческое мастерство";
    String wednesday = "\n" +
            "08:30 – 09:50 Математика\n" +
            "10:00 – 11:20 Химия\n" +
            "11:30 – 12:50 Обеденный перерыв\n" +
            "13:00 –14.20 География\n" +
            "14.30–15.50 Информатика\n" +
            "16.00–17.20 Свободное время / подготовка к занятиям";
    String thursday = "\n" +
            "08.30-09.50 История\n" +
            "10.00-11.20 Физика\n" +
            "11.30-12.50 Обеденный перерыв\n" +
            "13.00-14.20 Литература\n" +
            "14.30-15.50 Английский язык\n" +
            "16.00-17.20 Спорт / фитнес";
    String friday = "\n" +
            "08.30-09.50 Биология\n" +
            "10.00-11.20 Химия\n" +
            "11.30-12.50 Обеденный перерыв\n" +
            "13.00-14.20 Математика\n" +
            "14.30-15.50 Проектная работа / групповые задания";
    String saturday = "\n" +
            "10:00 – 12:00 Творческое занятие или свободное время\n" +
            "12:30 – Обеденный перерыв\n" +
            "13:30 – Спортивные активности или прогулки";

    String schedule = "\nПонедельник:" + monday +
            "\n\nВторник:" + tuesday +
            "\n\nСреда:" + wednesday +
            "\n\nЧетверг:" + thursday +
            "\n\nПятница:" + friday +
            "\n\nСуббота:" + saturday;

    @Override
    public void onUpdateReceived(Update update) {
        forWorkWithCommands(update);
    }

    public Bot() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            forWorkWithReminder();
        }, 0, 30, TimeUnit.SECONDS);
    }

    public void forWorkWithReminder() {
        LocalDateTime now = LocalDateTime.now();
        List<Case> listToRemove = new ArrayList<>();

        for (Case nowCase : listCases) {
            LocalDateTime reminderTime = LocalDateTime.parse(nowCase.getDateTime());

            if (reminderTime.minusHours(1).isBefore(now) || reminderTime.minusHours(1).isEqual(now)) {
                String[] arrayStrDateTime = nowCase.getDateTime().toString().split("T");
                String[] arrayHoursMinute = arrayStrDateTime[1].split(":");

                int hours = Integer.parseInt(arrayHoursMinute[0]);
                int minutes = Integer.parseInt(arrayHoursMinute[1]);
                LocalTime nowTime = LocalTime.of(hours, minutes, 0);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(nowCase.getUserId());
                sendMessage.setText("Напоминание!" +
                        "\nв " + nowTime + " у вас " + nowCase.getNameCase());
                try {
                    execute(sendMessage);
                    listToRemove.add(nowCase);
                } catch (Exception exception) {
                    exception.getMessage();
                }
            }
        }
        listCases.removeAll(listToRemove);
        try {
            objectMapper.writeValue(new File("src/main/resources/data/cases.json"), listCases);
        } catch (Exception exception) {
            exception.getMessage();
        }
    }

    public void forWorkWithCommands(Update update) {
        if (update.hasMessage()) {
            Long idUser = update.getMessage().getFrom().getId();
            String command = update.getMessage().getText();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(idUser);

            if (command.equals("/start")) {
                sendMessage.setText("Привет, " + update.getMessage().getFrom().getFirstName() +
                        ". Это бот-ассистент для ученков и учителей колледжа 'Сириус'" +
                        "\nЧтобы узнать расписание на сегодня, введите /today" +
                        "\nВсе команды - /help");
            } else if (command.equals("/help")) {
                sendMessage.setText("/today - Рассписание на сегодня" +
                        "\n/tommorow - Рассписание на завтра" +
                        "\n/week - Рассписание на неделю" +
                        "\n/add - Добавление события и напоминания" +
                        "\n/schedule - Информация о том, как добавить событие");
            } else if (command.equals("/today")) {
                if (dayOfWeek.equals("пн")) {
                    sendMessage.setText("Рассписание на понедельник:" + monday);
                } else if (dayOfWeek.equals("вт")) {
                    sendMessage.setText("Рассписание на вторник:" + tuesday);
                } else if (dayOfWeek.equals("ср")) {
                    sendMessage.setText("Рассписание на среду:" + wednesday);
                } else if (dayOfWeek.equals("чт")) {
                    sendMessage.setText("Рассписание на четверг:" + thursday);
                } else if (dayOfWeek.equals("пт")) {
                    sendMessage.setText("Рассписание на пятницу:" + friday);
                } else if (dayOfWeek.equals("сб")) {
                    sendMessage.setText("Рассписание на субботу:" + saturday);
                } else if (dayOfWeek.equals("вс")) {
                    sendMessage.setText("Сегодня выходной, рассписание на понедельник:" + monday);
                }
            } else if (command.equals("/tommorow")) {
                if (dayOfWeek.equals("пн")) {
                    sendMessage.setText("Рассписание на завтра:" + tuesday);
                } else if (dayOfWeek.equals("вт")) {
                    sendMessage.setText("Рассписание на завтра:" + wednesday);
                } else if (dayOfWeek.equals("ср")) {
                    sendMessage.setText("Рассписание на завтра:" + thursday);
                } else if (dayOfWeek.equals("чт")) {
                    sendMessage.setText("Рассписание на завтра:" + friday);
                } else if (dayOfWeek.equals("пт")) {
                    sendMessage.setText("Рассписание на завтра:" + saturday);
                } else if (dayOfWeek.equals("сб")) {
                    sendMessage.setText("Завтра выходной, рассписание на понедельник:" + monday);
                } else if (dayOfWeek.equals("вс")) {
                    sendMessage.setText("Рассписание на завтра:" + monday);
                }
            } else if (command.equals("/week")) {
                sendMessage.setText("Рассписание на неделю:" + schedule);
            } else if (command.startsWith("/add")) {
                String[] arrayCommand = command.split("\s");
                if (arrayCommand.length != 4) {
                    sendMessage.setText("Комманда введена неверно. Правильно оформление по команде /schedule");
                    try {
                        execute(sendMessage);
                    } catch (Exception exception) {
                        exception.getMessage();
                    }
                    return;
                }
                String currentNameCase = arrayCommand[1];

                if (!arrayCommand[2].contains("-")) {
                    sendMessage.setText("Некорректный формат даты. \nПравильное оформление по команде /schedule");
                    try {
                        execute(sendMessage);
                    } catch (Exception exception) {
                        exception.getMessage();
                    }
                    return;
                }

                if (!arrayCommand[3].contains(":")) {
                    sendMessage.setText("Некорректный формат времени. \nПравильное оформление по команде /schedule");
                    try {
                        execute(sendMessage);
                    } catch (Exception exception) {
                        exception.getMessage();
                    }
                    return;
                }
                String[] arrayStrDate = arrayCommand[2].split("-");
                int currentYear = Integer.parseInt(arrayStrDate[0]);
                int currentMonth = Integer.parseInt(arrayStrDate[1]);
                int currentDay = Integer.parseInt(arrayStrDate[2]);

                if (arrayStrDate.length != 3) {
                    sendMessage.setText("Некорректный формат даты. \nПравильное оформление по команде /schedule");
                    try {
                        execute(sendMessage);
                    } catch (Exception exception) {
                        exception.getMessage();
                    }
                    return;
                }

                String[] arrayStrTime = arrayCommand[3].split(":");
                int currentHours = Integer.parseInt(arrayStrTime[0]);
                int currentMinutes = Integer.parseInt(arrayStrTime[1]);

                if (arrayStrTime.length != 2) {
                    sendMessage.setText("Некорректный формат времени. \nПравильное оформление по команде /schedule");
                    try {
                        execute(sendMessage);
                    } catch (Exception exception) {
                        exception.getMessage();
                    }
                    return;
                }
                try {
                    LocalDateTime currentLocalDateTime = LocalDateTime.of(currentYear, currentMonth, currentDay, currentHours, currentMinutes);
                    if (LocalDateTime.now().isAfter(currentLocalDateTime)) {
                        sendMessage.setText("Дата и вермя события не может быть раньше, чем дата и время настоящего времени");
                        try {
                            execute(sendMessage);
                        } catch (Exception exception) {
                            exception.getMessage();
                        }
                        return;
                    }
                    Case currentCase = new Case(currentNameCase, currentLocalDateTime.toString(), idUser);
                    listCases.add(currentCase);
                    sendMessage.setText("Событие " + currentNameCase + " добавлено на " + arrayCommand[2] + " в " + arrayCommand[3]);
                    try {
                        objectMapper.writeValue(new File("src/main/resources/data/cases.json"), listCases);

                    } catch (Exception exception) {
                        exception.getMessage();
                    }
                } catch (DateTimeException exception) {
                    sendMessage.setText("Некоррекнтый формат даты или времени \nПравильное оформление по команде /schedule");
                }

            } else if (command.equals("/schedule")) {
                sendMessage.setText("Чтобы добавить событие и напоминание о нём, введите: " +
                        "\n/add Название события(Одним словом) дата(ГГГГ-ММ-ДД) время(ЧЧ:ММ)" + "\n\nПример: " +
                        "/add Контрольная 2025-07-10 14:30");
            }

            try {
                execute(sendMessage);
            } catch (Exception exception) {
                exception.getMessage();
            }
        }
    }


    @Override
    public String getBotUsername() {
        return "@sirius_students_assistant_bot";
    }

    @Override
    public String getBotToken() {
        return "8106400462:AAFwESa7l3qxlNJVt2NLpJuXuTtTuvYTtgc";
    }
}
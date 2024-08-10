import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.github.sarxos.webcam.Webcam;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {

    private static final GpioController gpio = GpioFactory.getInstance();
    private static final GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "LED", PinState.LOW);

    public static void main(String[] args) {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(new TelegramBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                detectIntruder();
            }
        }, 0, 2000); // Check every 2 seconds
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        String command = message.getText().toLowerCase();

        if (message.hasText()) {
            if (command.contains("on")) {
                sendTelegramMessage("Turned on LED", chatId);
                led.high();
            } else if (command.contains("off")) {
                sendTelegramMessage("Turned off LED", chatId);
                led.low();
            }
        } else if (message.hasVoice()) {
            String fileId = message.getVoice().getFileId();
            try {
                String audioFile = downloadVoiceMessage(fileId);
                command = processVoiceMessage(audioFile);
                Files.delete(Paths.get(audioFile));

                if (command.contains("on")) {
                    sendTelegramMessage("Turned on LED", chatId);
                    led.high();
                } else if (command.contains("off")) {
                    sendTelegramMessage("Turned off LED", chatId);
                    led.low();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendTelegramMessage(String message, String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String processVoiceMessage(String audioFile) throws Exception {
        // Implement your speech recognition here
        return "";
    }

    private String downloadVoiceMessage(String fileId) throws Exception {
        String token = "{BOT TOKEN ID}";
        String url = "https://api.telegram.org/bot" + token + "/getFile?file_id=" + fileId;
        HttpResponse<String> response = Unirest.get(url).asString();
        String filePath = response.getBody().getJSONObject("result").getString("file_path");
        String fileUrl = "https://api.telegram.org/file/bot" + token + "/" + filePath;

        File audioFileOgg = new File("/tmp/" + fileId + ".ogg");
        Files.copy(Unirest.get(fileUrl).asInputStream(), audioFileOgg.toPath());

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFileOgg);
        File audioFileWav = new File("/tmp/" + fileId + ".wav");
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFileWav);

        return audioFileWav.getAbsolutePath();
    }

    private static void detectIntruder() {
        int distance = readUltrasonicSensor();
        if (distance < 20) {
            System.out.println("Intruder detected at Distance: " + distance + "cm");
            sendTelegramMessage("Intruder detected! at " + distance + "cm", "{MAIN USER CHAT ID}");
        }
    }

    private static int readUltrasonicSensor() {
        try {
            // Implement your sensor reading here
            return 100; // Placeholder value
        } catch (Exception e) {
            e.printStackTrace();
            return 100;
        }
    }

    @Override
    public String getBotUsername() {
        return "{BOT USERNAME}";
    }

    @Override
    public String getBotToken() {
        return "{BOT TOKEN ID}";
    }
}

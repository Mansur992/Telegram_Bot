package az.mammadov.exchange_rates_bot.bot;

import az.mammadov.exchange_rates_bot.exception.ServiceException;
import az.mammadov.exchange_rates_bot.service.ExchangeRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.rmi.server.ServerCloneException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ExchangeRatesBot extends TelegramLongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRatesBot.class);

    private static final String START = "/start";
    private static final String USD = "/usd";
    private static final String EUR = "/eur";
    private static final String HELP = "/help";

    @Autowired
    private ExchangeRateService exchangeRateService;


    public ExchangeRatesBot(@Value("${bot.token}") String botToken){
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()){
            return;
        }
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        switch (message){
            case START -> {
                String userName = update.getMessage().getChat().getUserName();
                startCommand(chatId,userName);
            }
            case USD -> usdCommand(chatId);
            case EUR -> eurCommand(chatId);
            case HELP -> helpCommand(chatId);
            default -> unknownComman(chatId);
        }
    }

    @Override
    public String getBotUsername() {
        return "mansur_mammadov_e_bot";
    }

    private void sendMessage(Long chatId,String text){
        var chatIdStr = String.valueOf(chatId);
        var sendMEssage = new SendMessage(chatIdStr,text);
        try {
            execute(sendMEssage);
        }catch (TelegramApiException e){
            LOG.error("Ошибка отправки сообщения", e);
        }
    }



    private void usdCommand(Long chatId){
        String formattedText;
        try {
            var usd = exchangeRateService.getUSDExchangeRate();
            var text = "Курс доллара на %s составляет %s рублей";
            formattedText = String.format(text, LocalDate.now(), usd);
        }catch (ServiceException e){

            LOG.error("Ошибка получения курса доллара", e);
            formattedText = "Не удалось получить текущий курс доллара. Попробуйте позже";
        } catch (ServerCloneException e) {
            throw new RuntimeException(e);
        }
        sendMessage(chatId,formattedText);
    }


    private void eurCommand(Long chatId){
        String formattedtext;
        try {
            var eur = exchangeRateService.getEURExchangeRate();
            var text = "Курс евро на %s составляет %s рублей";
            formattedtext = String.format(text,LocalDate.now(),eur);
        }catch (ServiceException e){
            LOG.error("Ошибка получения курса евро", e);
            formattedtext = "Не удалось получить текущий курс евро. Попробуйте позже.";
        } catch (ServerCloneException e) {
            throw new RuntimeException(e);
        }
        sendMessage(chatId,formattedtext);
    }

    private void helpCommand(Long chatId){
        var text = """
                Справочная информация по боту
                
                Для получения текущих курсов валют воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                """;
        sendMessage(chatId,text);
    }

    public void unknownComman(Long chatId){
        var text = "Не удалось распознать команду";
        sendMessage(chatId, text);
    }


    private void startCommand(Long chatId, String userName){
        var text = """
                Добро пожаловать в бот, %s!
                
                Здесь Вы сможете узнать официальные курсы валют на сегодня, установленные ЦБ РФ.
                
                Для этого воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                
                Дополнительные команды:
                /help - получение справки
                """;
        var formattedText = String.format(text,userName);
        sendMessage(chatId,formattedText);
    }
}

package az.mammadov.exchange_rates_bot.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.rmi.server.ServerCloneException;

@Component
public class CbrClient {
    @Autowired
    private OkHttpClient client;

    @Value("${cbr.currency.rates.xml.url}")
    private String url;

    public String getCurrencyRateXml() throws ServerCloneException {
        var request = new Request.Builder()
                .url(url)
                .build();

        try (var response = client.newCall(request).execute();){
            var body = response.body();
            return body == null?null:body.string();

        }catch (IOException e){
            throw new ServerCloneException("Ошибка получения курсов валют",e);
        }
    }

}

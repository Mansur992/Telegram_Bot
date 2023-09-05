package az.mammadov.exchange_rates_bot.service;

import az.mammadov.exchange_rates_bot.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.rmi.server.ServerCloneException;


public interface ExchangeRateService {
    String getUSDExchangeRate() throws ServiceException, ServerCloneException;
    String getEURExchangeRate() throws ServiceException, ServerCloneException;


}

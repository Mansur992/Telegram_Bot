package az.mammadov.exchange_rates_bot.service.impl;

import az.mammadov.exchange_rates_bot.client.CbrClient;
import az.mammadov.exchange_rates_bot.exception.ServiceException;
import az.mammadov.exchange_rates_bot.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.print.Doc;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.rmi.server.ServerCloneException;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private static final String USD_XPATH = "/ValCurs//Valute[@ID='R01235']/Value";
    private static final String EUR_XPATH = "/ValCurs//Valute[@ID='R01239']/Value";

    @Autowired
    private CbrClient client;

    @Override
    public String getUSDExchangeRate() throws ServiceException, ServerCloneException {
        var xml = client.getCurrencyRateXml();
        return extractCurrencyValueFromXML(xml,USD_XPATH);
    }

    @Override
    public String getEURExchangeRate() throws ServiceException, ServerCloneException {
        var xml = client.getCurrencyRateXml();
        return extractCurrencyValueFromXML(xml,EUR_XPATH);
    }

    private static String extractCurrencyValueFromXML(String xml, String xpathExpression) throws ServiceException {
        var source = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            var document = (Document)xpath.evaluate("/",source, XPathConstants.NODE);

            return xpath.evaluate(xpathExpression,document);
        }catch (XPathExpressionException e){
            throw new ServiceException("Не удалось распарсить XML", e);
        }
    }
}

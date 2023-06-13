import com.example.stockmarket.service.WebCurrencyService;
import com.example.stockmarket.service.response.WebCurrencyServiceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class WebCurrencyServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private WebCurrencyService webCurrencyService = new WebCurrencyService(restTemplate);

//    @Test
//    public void isValidTest() {
//        WebCurrencyServiceResponse webCurrencyServiceResponse = restTemplate.getForObject(url, WebCurrencyServiceResponse.class, currencyPair, currencyServiceKey);
//        Mockito
//                .when(restTemplate.getForObject(url, WebCurrencyServiceResponse.class, currencyPair, currencyServiceKey))
//                .thenReturn()
//    }

    @Test
    public void convertTest() {
        String currencyServiceKey = "https://www.currate.ru";
        String currencyPair = "USDRUB";
        Map<String, String> response = new HashMap<>();
        response.put("USDRUB", "64.1824");
        WebCurrencyServiceResponse webCurrencyServiceResponse = new WebCurrencyServiceResponse();
        webCurrencyServiceResponse.setStatus("200");
        webCurrencyServiceResponse.setMessage("rates");
        webCurrencyServiceResponse.setData(response);
        Mockito
                .when(restTemplate.getForObject("https://www.currate.ru/api/?get=rates&pairs={pair}&key={key}", WebCurrencyServiceResponse.class, currencyPair, currencyServiceKey)).thenReturn(new WebCurrencyServiceResponse())
                .thenReturn((webCurrencyServiceResponse));
    }
}

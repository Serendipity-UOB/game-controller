package com.serendipity.service;

import com.serendipity.gameController.GameControllerApplication;
import com.serendipity.gameController.service.exchangeService.ExchangeServiceImpl;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GameControllerApplication.class)
public class ExchangeServiceTest {

    @Autowired
    ExchangeServiceImpl exchangeService;

}

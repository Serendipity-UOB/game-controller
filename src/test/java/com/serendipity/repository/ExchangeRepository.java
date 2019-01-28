package com.serendipity.repository;

import com.serendipity.gameController.GameControllerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GameControllerApplication.class)
public class ExchangeRepository {

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Test
    public void test() {}

}

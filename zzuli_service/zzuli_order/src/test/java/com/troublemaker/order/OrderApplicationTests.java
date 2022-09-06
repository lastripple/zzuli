package com.troublemaker.order;

import com.troublemaker.order.excute.DoOrderTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



/**
 * @author Troublemaker
 * @date 2022- 04 29 10:46
 */
@SpringBootTest
@Slf4j
public class OrderApplicationTests {

    @Autowired
    private DoOrderTask doOrdertask;

    @Test
    void contextLoads() {
        log.info("-----------------测试启动-------------------");
        doOrdertask.start();
        log.info("-----------------测试完毕-------------------");
    }

}

package com.mustafadagher.widgets.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class WidgetsApiControllerTest {
    @Autowired
    private WidgetsApiController widgetsApiController;

    @Test
   void testController(){
        assertNotNull(widgetsApiController);
    }
}
package com.lxc.Competitioninformationsystem;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.lxc.Competitioninformationsystem.entity.Notification;
import org.junit.jupiter.api.Test;

public class FooTest {

    @Test
    void testFastJson() {
        Notification notification = new Notification();
        notification.setCmptName("Connect X");
        notification.setBrief("this is a brief");
        notification.setCoverImageUrl("this is a iconUrl");
        notification.setDetailPageUrl("this is a coverImageUrl");

//        String result = JSON.toJSONString(notification);
        String result = new Gson().toJson(notification);

        System.out.println(result);
    }
}

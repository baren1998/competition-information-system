package com.lxc.Competitioninformationsystem;

import com.lxc.Competitioninformationsystem.entity.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class CompetitionInformationSystemApplicationTests {

	@Autowired
	RedisTemplate<String, Serializable> redisTemplate;
	@Autowired
	StringRedisTemplate stringRedisTemplate;

	private final String KEY = "notification";

	@Test
	void testObject() {
		Notification notification = new Notification();
		redisTemplate.opsForValue().set("notification", notification);
		Notification notification1 = (Notification) redisTemplate.opsForValue().get("notification");
		System.out.println(notification1);
	}

	@Test
	void testList() {
		List<Notification> notificationList = new ArrayList<>();
		for(int i = 0; i < 5; i++) {
			Notification notification = new Notification();
			notificationList.add(notification);
		}
		//循环向 list 左添加值
		notificationList.forEach(value -> redisTemplate.opsForList().leftPush(KEY, value));
		// 获取值
		redisTemplate.opsForList().range(KEY, 0, 10).stream().map(value -> (Notification) value).forEach(System.out::println);
	}

	@Test
	void testSize() {
		System.out.println(redisTemplate.opsForList().size("abc"));
	}

	@Test
	void testNotification() {
		Notification notification = new Notification();
		notification.setCmptName("Connect X");
		notification.setBrief("this is a brief");
		notification.setCoverImageUrl("this is a iconUrl");
		notification.setDetailPageUrl("this is a coverImageUrl");
		pushNotification(notification);


	}

	// 将新通知推送到Redis服务器上
	private void pushNotification(Notification notification) {
		// 将新通知追加到redis列表中
		redisTemplate.opsForList().leftPush("notification", notification);
		// 更新最后一次推送通知的时间
		String lastNotifyTimeMillis = String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
		stringRedisTemplate.opsForValue().set("lastNotify", lastNotifyTimeMillis);
	}
}

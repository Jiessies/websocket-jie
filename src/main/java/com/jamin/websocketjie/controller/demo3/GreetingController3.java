package com.jamin.websocketjie.controller.demo3;

import com.jamin.websocketjie.login.Authentication;
import com.jamin.websocketjie.model.Greeting;
import com.jamin.websocketjie.model.HelloMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Date;
/*
 *3.1 在处理消息之后发送消息
    正如前面看到的那样，使用 @MessageMapping 或者 @SubscribeMapping 注解可以处理客户端发送过来的消息，并选择方法是否有返回值。
    如果 @MessageMapping 注解的控制器方法有返回值的话，返回值会被发送到消息代理，只不过会添加上"/topic"前缀。可以使用@SendTo 重写消息目的地；
    如果 @SubscribeMapping 注解的控制器方法有返回值的话，返回值会直接发送到客户端，不经过代理。如果加上@SendTo 注解的话，则要经过消息代理。

    @MessageMapping 指定目的地是“/app/marco”（“/app”前缀是隐含的，因为我们将其配置为应用的目的地前缀）。
 */
@Controller
public class GreetingController3 {

    /*
     * @SendToUser("/queue/notifications") 对应前端地址为 /user/queue/notifications-username
     * 但是问题来了，这个username是怎么来的呢？就是通过 principal 参数来获得的。那么，principal 参数又是怎么来的呢？需要在spring-websocket 的配置类中重写 configureClientInboundChannel 方法，添加上用户的认证。
     */
    private final SimpMessagingTemplate messagingTemplate;

    /*
     * 实例化Controller的时候，注入SimpMessagingTemplate
     */
    @Autowired
    public GreetingController3(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageExceptionHandler(Exception.class)//如果有错误会发送到特定的目的地上，然后用户从该目的地上订阅消息，从而用户就能知道自己出现了什么错误啦
    @MessageMapping("/demo3/hello/{destUsername}")
    @SendToUser("/demo3/greetings")
    public Greeting greeting(@DestinationVariable String destUsername, HelloMessage message, StompHeaderAccessor headerAccessor) throws Exception {

        Authentication user = (Authentication) headerAccessor.getUser();

        String sessionId = headerAccessor.getSessionId();

        Greeting greeting = new Greeting(user.getName(), "sessionId: " + sessionId + ", message: " + message.getMessage());

        /*
         * 对目标进行发送信息
         * convertAndSendToUser 方法最终会把消息发送到 /user/username/demo3/greetings 目的地上。
         * username==destUsername
         */
        messagingTemplate.convertAndSendToUser(destUsername, "/demo3/greetings", greeting);


        return new Greeting("系统", new Date().toString() + "消息已被推送。");
    }

}

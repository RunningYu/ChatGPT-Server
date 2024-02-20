package chatgptserver.netty.handler;

import chatgptserver.netty.Common.Result;
import chatgptserver.netty.command.ChatMessage;
import chatgptserver.netty.im.IMserver;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2023/5/22
 */
@Component
@Slf4j
public class ConnectionHandler {
    public void execute(ChannelHandlerContext ctx, ChatMessage chatMessage) {
        log.info("ConnectionHandler execute chatMessage:{}" + chatMessage);
        // 判断是否已经有同名的人（即同一个人）已经在线
        if (IMserver.USERS.containsKey(chatMessage.getToken())) {
            ctx.channel().writeAndFlush(Result.success("已经与服务端建立过连接"));
            log.info("ConnectionHandler execute --> 已经与服务端建立过连接");
            return;
        }

        // 加到映射表里
        IMserver.USERS.put(chatMessage.getToken(), ctx.channel());
        ctx.channel().writeAndFlush(Result.success("与服务端连接建立成功"));
        log.info("ConnectionHandler execute --> 与服务端连接建立成功");
    }
}

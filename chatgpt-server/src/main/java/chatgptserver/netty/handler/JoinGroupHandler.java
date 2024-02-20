package chatgptserver.netty.handler;

import chatgptserver.netty.Common.Result;
import chatgptserver.netty.im.IMserver;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2023/5/22
 */
public class JoinGroupHandler {
    public static void execute(ChannelHandlerContext ctx) {
        IMserver.GROUP.add(ctx.channel());
        ctx.channel().writeAndFlush(Result.success("加入系统默认群聊成功~"));
    }
}

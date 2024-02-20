package chatgptserver.netty.im;

import chatgptserver.netty.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2023/5/22
 */
public class IMserver {

    /** 保存映射关系  */
    public static final Map<String, Channel> USERS = new ConcurrentHashMap<>(1024);

    /**
     * 用系统提供的一个默认的群聊组（ netty自带的 ChannelGroup ）
     * 我们每一个消息通道都有一个channel，我们可以把channel 注册到 ChannelGroup 组中去,
     * 这样它发送消息的时候就，会给每一个channel发一条消息
     * */
    public static final ChannelGroup GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void start() {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        // 绑定端口
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // 添加http编解码器
                        pipeline.addLast(new HttpServerCodec())
                                // 支持大数据流
                                .addLast(new ChunkedWriteHandler())
                                // 对http消息做聚合操作，FullHttpRequest、FullHttpResponse
                                .addLast(new HttpObjectAggregator(1024 * 64))
                                // webSocket
                                .addLast(new WebSocketServerProtocolHandler("/"))
                                .addLast(new WebSocketHandler());
                    }
                });

        ChannelFuture future = bootstrap.bind(8088);
        System.out.println("-------------------------");

    }

}

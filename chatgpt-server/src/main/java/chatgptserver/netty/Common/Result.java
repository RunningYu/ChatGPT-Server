package chatgptserver.netty.Common;

import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2023/5/22
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private String name;

    private LocalDateTime time;

    private String message;

    public static TextWebSocketFrame fail(String message) {
        return new TextWebSocketFrame(JSON.toJSONString(
                new Result("系统消息", LocalDateTime.now(), message))
        );
    }

    public static TextWebSocketFrame success(String message) {
        return new TextWebSocketFrame(JSON.toJSONString(new Result("系统消息", LocalDateTime.now(), message)));
    }

    public static TextWebSocketFrame success(String user, String message) {
        return new TextWebSocketFrame(JSON.toJSONString(new Result(user, LocalDateTime.now(), message)));
    }

}

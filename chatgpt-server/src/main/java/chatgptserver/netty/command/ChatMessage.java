package chatgptserver.netty.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2023/5/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    /** 操作类型 1-建立连接 2-聊天消息 */
    private Integer code;

    /** 消息类型，1-私有消息 or 2-群聊消息 */
    private Integer type;

    /** 发送人的token */
    private String token;

    /** 接收人的token */
    private String targetToken;


    /** 内容 */
    private String content;


    /** 发送人的token */
    private String senderCode;

    /** 接收人的token */
    private String targetCode;

}










































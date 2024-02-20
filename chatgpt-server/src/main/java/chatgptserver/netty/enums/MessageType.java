package chatgptserver.netty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2023/5/22
 */
@Getter
@AllArgsConstructor
public enum MessageType {

    // 私聊
    PRIVATE(1),

    // 群聊
    GROUP(2),

    // 错误
    ERROR(-1);

    private Integer type;

    public static MessageType match(Integer type) {
        for (MessageType value : MessageType.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return ERROR;
    }

}

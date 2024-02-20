package chatgptserver.netty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2023/5/22
 */
@Getter
@AllArgsConstructor
public class CommandType {
    /**
     * 建立连接
     */
    public static final int CONNECTION = 1;
    /**
     * 聊天消息
     */
    public static final int CHAT = 2;

    /**
     * 取聊消息
     */
    public static final int JOIN_GROUP = 3;

    public static final int ERROR = -1;

//    public static Integer match(Integer code) {
//        for ( CommandType value : CommandType.values() ) {
//            System.out.println( "code:" + code + " ----> value:" + value );
//            if (value.getCode().equals(code)) {
//                return value.getCode();
//            }
//        }
//        return ERROR.getCode();
//    }
}

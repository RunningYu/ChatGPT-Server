package chatgptserver.netty.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模型
 * @author : 其然乐衣Letitbe
 * @date : 2023/5/22
 */

@Data
@AllArgsConstructor   // 全参构造
@NoArgsConstructor    // 无参构造
public class Command {

    /**
     * 连接信息码，用于表示是 连接、聊天...
     */
    private Integer code;

    /**
     * 学号
     */
    private String studentId;
}

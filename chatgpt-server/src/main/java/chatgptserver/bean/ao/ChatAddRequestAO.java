package chatgptserver.bean.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/5 15:01
 */
@ApiModel("新建对话请求体")
@Data
public class ChatAddRequestAO {

    @ApiModelProperty(value = "用户的code", required = true)
    private String userCode;

    @ApiModelProperty(value = "1-文心一言 2-chatgpt", required = true)
    private Integer chatType;

    @ApiModelProperty(value = "新建的对话的名字", required = true)
    private String chatName;

}

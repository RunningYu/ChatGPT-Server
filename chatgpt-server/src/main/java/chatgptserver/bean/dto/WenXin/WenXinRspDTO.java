package chatgptserver.bean.dto.WenXin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/3 16:22
 */
@ApiModel("文心一言机器人智能回复响应体")
@Data
public class WenXinRspDTO {

    @ApiModelProperty("本轮对话的id")
    private String id;

    private String object;

    @ApiModelProperty("时间戳")
    private long created;

    @ApiModelProperty("对话返回结果")
    private String result;

    private boolean is_truncated;

    private boolean need_clear_history;

    private String finish_reason;

    private Usage usage;

}
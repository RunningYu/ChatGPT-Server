package chatgptserver.bean.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:12
 */
@ApiModel("消息返回体")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessagesResponseAO {

    @ApiModelProperty("聊天记录列表")
    private List<MessagesAO> list;

    @ApiModelProperty("总数")
    private Integer total;

    @ApiModelProperty("是都还有")
    private Boolean hasMore;

    @ApiModelProperty("聊天名称")
    private String chatName;

}

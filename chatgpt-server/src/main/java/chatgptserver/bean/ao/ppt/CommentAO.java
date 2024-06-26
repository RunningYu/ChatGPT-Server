package chatgptserver.bean.ao.ppt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/5/20
 */
@Data
public class CommentAO {

    @ApiModelProperty("评论的code")
    private String commentCode;

    @ApiModelProperty("ppt的code")
    private String pptCode;

    @ApiModelProperty(value = "用户的code", required = true)
    private String userCode;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("用户头像")
    private String headshot;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("回复量")
    private Integer replyAmount;

    @ApiModelProperty("创建时间")
    private String createTime;

}

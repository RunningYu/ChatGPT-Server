package chatgptserver.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageCreatePO {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("聊天code")
    private String chatCode;

    @ApiModelProperty("图片描述")
    private String content;

    @ApiModelProperty("图片链接")
    private String url;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;


    public ImageCreatePO(Integer userId, String chatCode, String content, String url) {
        this.userId = userId;
        this.chatCode = chatCode;
        this.content = content;
        this.url = url;
    }
}

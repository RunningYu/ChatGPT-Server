package chatgptserver.bean.ao.ppt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/26
 */
@Data
public class PptAO {

    @ApiModelProperty("true：收藏，false：没收藏")
    private Boolean isCollected;

    @ApiModelProperty("PPT的code")
    private String pptCode;

    @ApiModelProperty("用户code")
    private String userCode;

    @ApiModelProperty("用户c用户名称ode")
    private String username;

    @ApiModelProperty("一级分类")
    private String firstKind;

    @ApiModelProperty("二级分类")
    private String secondKind;

    @ApiModelProperty("PPT主题")
    private String title;

//    @ApiModelProperty("问题")
//    private String content;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("PPT封面url")
    private String coverUrl;

    @ApiModelProperty("PPT文件url")
    private String pptUrl;

    @ApiModelProperty("评分")
    private Double score;

    @ApiModelProperty("收藏量")
    private Integer collectAmount;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}

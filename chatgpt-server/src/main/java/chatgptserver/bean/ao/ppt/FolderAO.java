package chatgptserver.bean.ao.ppt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/5/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolderAO {

    @ApiModelProperty("用户code")
    private String userCode;

    @ApiModelProperty("文件夹code")
    private String folderCode;

    @ApiModelProperty("文件夹")
    private String folder;

    @ApiModelProperty("true：收藏，false：没收藏")
    private Boolean isCollected;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public FolderAO(String userCode, String folder) {
        this.userCode = userCode;
        this.folder = folder;
    }
}

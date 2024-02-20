package chatgptserver.bean.dto.WenXin;

import com.google.gson.Gson;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/1/13
 */
@Data
public class ImageFlagDTO {

    public String totalAnswer=""; // 大模型的答案汇总

    // 环境治理的重要性  环保  人口老龄化  我爱我的祖国
    public String NewQuestion = "";
    public Boolean ImageAddFlag = false; // 判断是否添加了图片信息

    public  Gson gson = new Gson();

    // 个性化参数
    private String userId;
    private Boolean wsCloseFlag;

    private Boolean totalFlag=true; // 控制提示用户是否输入
}

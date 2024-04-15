package chatgptserver.bean.dto.XunFeiXingHuo.pptCreate;

import lombok.Data;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.List;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/15
 */
@Data
public class PptOutlineResponse {

    private String subTitle;

    private List<Chapters> chapters;

    private String fileUrl;

    private String end;

    private Integer id;

    private String title;

    private Integer fileType;

}

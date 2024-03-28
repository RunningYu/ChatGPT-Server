package chatgptserver.bean.dto.tongYiQianWen;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/28
 */
@Data
public class ImageCreateResultOutput {

    private String task_id;

    private String task_status;

    private List<Map<String, String>> results;

    private Map<String, Integer> task_metrics;

}

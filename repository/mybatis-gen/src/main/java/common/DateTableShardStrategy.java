package common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author csh9016
 * @date 2020/6/8
 */
public class DateTableShardStrategy implements ITableShardStrategy {

    private static final String DATE_PATTERN = "yyyyMMdd";

    @Override
    public String tableShard(String tableName) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            if (ShardParamContext.get() != null) {
                return tableName + "_" + sdf.format((Date) ShardParamContext.get());
            }else {
                return tableName + "_" + sdf.format(new Date());
            }
        }finally {
            ShardParamContext.remove();
        }

    }
}

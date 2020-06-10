package common;

/**
 * @author csh9016
 * @date 2020/6/9
 */
public class BusinessCodeShardStrategy implements ITableShardStrategy {

    @Override
    public String tableShard(String tableName) {
        if (ShardParamContext.get() != null) {
            String key = (String)ShardParamContext.get();
            int month = Integer.parseInt(key.substring(2, 4));
            return tableName + "_20" + key.substring(0, 2) + String.format("%02d", 3 * ((month + 2) / 3));
        }else {
            return tableName;
        }
    }

}

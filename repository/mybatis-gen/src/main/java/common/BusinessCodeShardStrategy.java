package common;

/**
 * @author csh9016
 * @date 2020/6/9
 */
public class BusinessCodeShardStrategy extends AbstractTableShardStrategy {

    @Override
    public String tableShard(String tableName) {
        if (ShardParamContext.get() != null) {
            String key = (String) ShardParamContext.get();
            int month = Integer.parseInt(key.substring(2, 4));
            String shard = "20" + key.substring(0, 2) + String.format("%02d", 3 * ((month + 2) / 3));
            setShard(shard);
            return tableName + "_" + shard;
        } else {
            return tableName;
        }
    }

}

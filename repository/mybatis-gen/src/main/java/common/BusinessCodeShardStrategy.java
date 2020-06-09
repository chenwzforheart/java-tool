package common;

/**
 * @author csh9016
 * @date 2020/6/9
 */
public class BusinessCodeShardStrategy implements ITableShardStrategy {

    @Override
    public String tableShard(String tableName) {
        if (ShardParamContext.get() != null) {
            return tableName + "_" + ShardParamContext.get();
        }else {
            return tableName;
        }
    }
}

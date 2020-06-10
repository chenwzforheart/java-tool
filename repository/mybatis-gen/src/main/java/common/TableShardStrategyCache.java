package common;

import common.mapper.CommonMapper;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author csh9016
 * @date 2020/6/10
 */
public class TableShardStrategyCache {

    private static CommonMapper commonMapper;

    private static ConcurrentHashMap<TableShard, ITableShardStrategy> cache = new ConcurrentHashMap<>();

    public static ITableShardStrategy getStrategy(TableShard tableShard) {
        if (!cache.containsKey(tableShard)) {
            Class<? extends ITableShardStrategy> strategyClazz = tableShard.shardStrategy();
            ITableShardStrategy strategy = null;
            try {
                strategy = strategyClazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (strategy instanceof AbstractTableShardStrategy) {
                ((AbstractTableShardStrategy) strategy).setCommonMapper(commonMapper);
                ((AbstractTableShardStrategy) strategy).setTable(tableShard.tableName());
            }
            cache.put(tableShard, strategy);
        }
        return cache.get(tableShard);
    }

    public static void setCommonMapper(CommonMapper commonMapper) {
        TableShardStrategyCache.commonMapper = commonMapper;
    }
}

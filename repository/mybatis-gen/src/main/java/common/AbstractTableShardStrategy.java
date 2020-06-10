package common;

import common.ITableShardStrategy;
import common.mapper.CommonMapper;

/**
 * @author csh9016
 * @date 2020/6/10
 */
public abstract class AbstractTableShardStrategy implements ITableShardStrategy {

    private CommonMapper commonMapper;

    private String table;

    private String shard;

    public void setCommonMapper(CommonMapper commonMapper) {
        this.commonMapper = commonMapper;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void initCurShard() {
        commonMapper.createTableLike(table + "_" + shard, table);
    }

    public void initNextShard() {
        commonMapper.createTableLike(table + "_next", table);
    }

    public void setShard(String shard) {
        if (this.shard == null || !this.shard.equals(shard)) {
            this.shard = shard;
            initCurShard();
        }
    }
}

package common;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author csh9016
 * @date 2020/6/1
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {
}

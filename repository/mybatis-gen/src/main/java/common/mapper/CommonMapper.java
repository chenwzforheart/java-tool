package common.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @author csh9016
 * @date 2020/6/9
 */
public interface CommonMapper {

    int createTableLike(@Param("newTable") String newTable, @Param("base") String base);
}

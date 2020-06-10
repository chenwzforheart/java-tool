package common;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import common.mapper.CommonMapper;
import example.model.XxlJobUser;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author csh9016
 * @date 2020/6/8
 */
public class MybatisPageTest {

    @Test
    public void select() throws IOException {
        SqlSession session = getSqlSession();
        //4.通过SqlSession执行Sql语句获取结果
        XxlJobUser userList = session.selectOne("example.mapper.XxlJobUserMapper.selectByPrimaryKey",1);
        System.out.println(userList.getUsername());
    }

    private SqlSession getSqlSession() throws IOException {
        MapperHelper mapperHelper = new MapperHelper();
        mapperHelper.ifEmptyRegisterDefaultInterface();

        String resource = "mybatis-config.xml";
        //1.流形式读取mybatis配置文件
        InputStream stream = Resources.getResourceAsStream(resource);
        //2.通过配置文件创建SqlSessionFactory
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(stream);
        //3.通过SqlSessionFactory创建sqlSession
        SqlSession session = sessionFactory.openSession();
        Collection<Class<?>> mappers = session.getConfiguration().getMapperRegistry().getMappers();
        for (Class<?> mapper : mappers) {
            //通用Mapper
            if (mapperHelper.isExtendCommonMapper(mapper)) {
                mapperHelper.processConfiguration(session.getConfiguration(), mapper);
            }
        }
        return session;
    }

    @Test
    public void page() throws Exception{
        SqlSession session = getSqlSession();
        Page page = PageHelper.startPage(1, 10, true);
        XxlJobUser user = new XxlJobUser();
        user.setUsername("user1");

        TableShardStrategyCache.setCommonMapper(session.getMapper(CommonMapper.class));

        ShardParamContext.set("20031911111481261732");
        List<XxlJobUser> result = session.selectList("example.mapper.XxlJobUserMapper.select", user);
        System.out.println(result.size());
    }

    @Test
    public void divideTable() throws IOException{
        SqlSession session = getSqlSession();
        HashMap<String, Object> param = new HashMap<>();
        param.put("newTable", "xxl_job_user_20200609");
        param.put("base", "xxl_job_user");
        session.update("common.mapper.CommonMapper.createTableLike",param );
    }
}

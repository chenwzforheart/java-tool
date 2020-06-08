package common;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author csh9016
 * @date 2020/6/8
 */
public class MybatisPageTest {

    @Test
    public void select() throws IOException {
        MapperHelper mapperHelper = new MapperHelper();
        mapperHelper.ifEmptyRegisterDefaultInterface();
        String resource = "mybatis-config.xml";
        //1.流形式读取mybatis配置文件
        InputStream stream = Resources.getResourceAsStream(resource);
        //2.通过配置文件创建SqlSessionFactory
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(stream);
        //3.通过SqlSessionFactory创建sqlSession
        SqlSession session = sessionFactory.openSession();
        //4.通过SqlSession执行Sql语句获取结果
        //XxlJobUser userList = session.selectOne("example.mapper.XxlJobUserMapper.selectByPrimaryKey",1);
        //System.out.println(userList.getUsername());
    }
}

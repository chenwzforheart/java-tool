package common;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author csh9016
 * @date 2020/6/8
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class DivideTablePlugin implements Interceptor {

    private static final ReflectorFactory defaultReflectorFactory = new DefaultReflectorFactory();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("开始拦截");
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler,
                SystemMetaObject.DEFAULT_OBJECT_FACTORY,
                SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                defaultReflectorFactory
        );
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        String id = mappedStatement.getId();
        id = id.substring(0, id.lastIndexOf('.'));
        Class clazz = Class.forName(id);

        // 获取TableShard注解
        TableShard tableShard = (TableShard)clazz.getAnnotation(TableShard.class);
        if ( tableShard != null ) {
            String tableName = tableShard.tableName();
            Class<? extends ITableShardStrategy> strategyClazz = tableShard.shardStrategy();
            ITableShardStrategy strategy = strategyClazz.newInstance();
            String newTableName = strategy.tableShard(tableName);
            // 获取源sql
            String sql = (String)metaObject.getValue("delegate.boundSql.sql");
            // 用新sql代替旧sql, 完成所谓的sql rewrite
            if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
                String suffix = newTableName.replaceAll(tableName, "");
                metaObject.setValue("delegate.boundSql.sql",selectSql(sql, suffix));
            }else {
                metaObject.setValue("delegate.boundSql.sql", sql.replaceAll(tableName, newTableName));
            }
        }
        return invocation.proceed();
    }

    public static String selectSql(String sql,String suffix) {
        Pattern p = Pattern.compile("(xxl_[_a-zA-Z]*)",Pattern.CASE_INSENSITIVE);
        StringBuffer sbf = new StringBuffer();
        Matcher m = p.matcher(sql);
        int index = 0;
        while (m.find()) {
            sbf.append(sql.substring(index, m.start()));
            sbf.append(m.group() + suffix);
            index = m.end();
        }
        sbf.append(sql.substring(index));
        return sbf.toString();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }
}

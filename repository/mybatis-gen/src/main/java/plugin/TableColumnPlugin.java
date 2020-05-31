package plugin;

import org.mybatis.generator.api.ConnectionFactory;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.JDBCConnectionFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @date 2020/6/1
 */
public class TableColumnPlugin extends PluginAdapter {

    private static String currentTable = "";

    private static ConnectionFactory connectionFactory;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        String tableName = introspectedTable.getFullyQualifiedTable().toString();
        if (!currentTable.equals(tableName)) {
            currentTable = tableName;

            //1.Table的注释
            String tableRemark = "";
            try {
                tableRemark = getTableRemark(introspectedTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            topLevelClass.addJavaDocLine("/**");
            topLevelClass.addJavaDocLine(" * " + tableRemark.replaceAll("\r|\n|\r\n", ""));
            topLevelClass.addJavaDocLine(" */");
        }

        String remark = introspectedColumn.getRemarks();
        if (remark != null && remark.trim().length() > 0 && !"null".equals(remark)) {
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * " + remark.replaceAll("\r|\n|\r\n", ""));
            field.addJavaDocLine(" */");
        }

        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    public static String getTableRemark(IntrospectedTable introspectedTable) throws SQLException {
        if (connectionFactory == null) {
            connectionFactory = new JDBCConnectionFactory(introspectedTable.getContext().getJdbcConnectionConfiguration());
        }
        Connection connection = connectionFactory.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        String tableName = introspectedTable.getFullyQualifiedTable().toString();
        ResultSet resultSet = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
        while (resultSet.next()) {
            String tableName1 = resultSet.getString("TABLE_NAME");
            return resultSet.getString("REMARKS");
        }

        return "";
    }
}

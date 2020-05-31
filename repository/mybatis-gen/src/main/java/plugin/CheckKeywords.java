package plugin;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.DataSourceConnectionFactory;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class CheckKeywords {

    public static void main(String[] args) {
        //*/表名前后坠处理，如+abcd，-abcd
        test("", "");
    }

    public static void test(String prefix, String suffix) {
        Map<String, String> db = GeneratorUtil.parseXml();

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(db.get("connectionURL"));
        dataSource.setUsername(db.get("userId"));
        dataSource.setPassword(db.get("password"));
        dataSource.setDriverClassName(db.get("driverClass"));

        DataSourceConnectionFactory connectionFactory = new DataSourceConnectionFactory(dataSource);
        String base = "<table tableName=\"%1s\" domainObjectName=\"%2s\"\n" +
                "               enableCountByExample=\"false\" enableDeleteByExample=\"false\"\n" +
                "               enableSelectByExample=\"false\" enableUpdateByExample=\"false\"\n" +
                "               enableInsert=\"false\" enableUpdateByPrimaryKey=\"false\"\n" +
                "               enableDeleteByPrimaryKey=\"false\"\n" +
                "        ></table>";
        try {
            Connection connection = connectionFactory.createConnection();
            ResultSet rs = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                String entityName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, processEntityName(tableName, prefix, suffix));
                System.out.println(String.format(base, tableName, entityName));
                getTableKeys(tableName, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTableKeys(String tableName, Connection connection) throws SQLException {
        HashSet<String> keys = Sets.newHashSet(KeyWord.keysMySQL5_7);
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet rs = metaData.getColumns(null, null, tableName, "%");
        while (rs.next()) {
            String column_name = rs.getString("COLUMN_NAME");
            if (keys.contains(column_name.toUpperCase())) {
                //System.out.println(column_name);
            }
        }
        return null;
    }

    public static String processEntityName(String tableName, String prefix, String suffix) {
        if (StringUtils.isNotEmpty(prefix)) {
            if (prefix.charAt(0) == '+') {
                tableName = prefix.substring(1) + "_" + tableName;
            } else if (prefix.charAt(0) == '-') {
                tableName = tableName.replaceFirst(prefix.substring(1), "");
            } else {
                //ignore
            }
        }
        if (StringUtils.isNotEmpty(suffix)) {
            if (suffix.charAt(0) == '+') {
                tableName = tableName + "_" + suffix.substring(1);
            } else if (suffix.charAt(0) == '-') {
                tableName = StringUtils.removeEnd(tableName, suffix.substring(1));
            } else {
                //ignore
            }
        }
        return tableName;
    }
}

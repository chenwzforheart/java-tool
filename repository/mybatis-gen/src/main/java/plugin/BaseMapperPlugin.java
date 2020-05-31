package plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by csh9016 on 2019/4/30.
 */
public class BaseMapperPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    private Set<String> mappers = new HashSet<String>();

    private static String currentTable = "";

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String mappers = this.properties.getProperty("mappers");
        for (String mapper : mappers.split(",")) {
            this.mappers.add(mapper);
        }
    }


    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        // 获取实体类
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

        // import接口
        for (String mapper : mappers) {
            interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(mapper + "<" + entityType.getShortName() + ">"));
        }

        // import实体类
        interfaze.addImportedType(entityType);

        return true;

    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        String tableName = introspectedTable.getFullyQualifiedTable().toString();
        if (!currentTable.equals(tableName)) {
            currentTable = tableName;
            //1.Table注解
            topLevelClass.addImportedType("javax.persistence.Table");
            topLevelClass.addAnnotation("@Table(name = \"" + tableName + "\")");
        }

        //使用通用Mapper时 添加ID自增的注解 无通用Mapper 请直接去掉
        if ("ID".equalsIgnoreCase(introspectedColumn.getActualColumnName())) {
            field.addAnnotation("@Id");
            field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)");
            topLevelClass.addImportedType("javax.persistence.Id");
            topLevelClass.addImportedType("javax.persistence.GeneratedValue");
            topLevelClass.addImportedType("javax.persistence.GenerationType");
        }

        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }
}

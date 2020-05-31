package plugin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mybatis.generator.internal.util.JavaBeansUtil.getGetterMethodName;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getSetterMethodName;


public class GeneratorUtil {

    public static void main(String[] args) throws IOException{
        parseXml();
    }

    public static Map<String,String> parseXml() {
        //1.获取xml解析文件的路径
        String path = GeneratorUtil.class.getClassLoader().getResource( "generatorConfig.xml" ).getPath();
        //2.解析xml文档，加载文档进内存,获取document对象
        Document document = null;
        try {
            document = Jsoup.parse(new File( path ),"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //3.获取元素对象Element，返回值是存了Element对象的ArrayList集合
        Elements jdbcConnection = document.getElementsByTag( "jdbcConnection" );
        //4.获取DB连接信息
        Map<String, String> db = new HashMap<>();
        db.put("driverClass", jdbcConnection.attr("driverClass"));
        db.put("connectionURL", jdbcConnection.attr("connectionURL"));
        db.put("userId", jdbcConnection.attr("userId"));
        db.put("password", jdbcConnection.attr("password"));
        return db;
    }

    public static String type2Name(String type) {
        int i = type.lastIndexOf('.');
        return type.substring(i + 1);
    }

    public static String type2Variable(String type) {
        int i = type.lastIndexOf('.');
        char first = type.charAt(i + 1);
        if (first >= 65 && first <= 90) {
            first = (char) (first + 32);
        }
        return first + type.substring(i + 2);
    }

    public static Method getGetter(Field field) {
        Method method = new Method();
        method.setName(getGetterMethodName(field.getName(), field
                .getType()));
        method.setReturnType(field.getType());
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(field.getName());
        sb.append(';');
        method.addBodyLine(sb.toString());
        return method;
    }

    public static Field getJavaBeansField(IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType fqjt = introspectedColumn.getFullyQualifiedJavaType();
        String property = introspectedColumn.getJavaProperty();

        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(fqjt);
        field.setName(property);

        return field;
    }

    public Method getJavaBeansGetter(IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType fqjt = introspectedColumn
                .getFullyQualifiedJavaType();
        String property = introspectedColumn.getJavaProperty();

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(fqjt);
        method.setName(getGetterMethodName(property, fqjt));

        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(property);
        sb.append(';');
        method.addBodyLine(sb.toString());

        return method;
    }

    public Method getJavaBeansSetter(IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType fqjt = introspectedColumn
                .getFullyQualifiedJavaType();
        String property = introspectedColumn.getJavaProperty();

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(getSetterMethodName(property));
        method.addParameter(new Parameter(fqjt, property));

        StringBuilder sb = new StringBuilder();
        if (introspectedColumn.isStringColumn()) {
            sb.append("this."); //$NON-NLS-1$
            sb.append(property);
            sb.append(" = "); //$NON-NLS-1$
            sb.append(property);
            sb.append(" == null ? null : "); //$NON-NLS-1$
            sb.append(property);
            sb.append(".trim();"); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
        } else {
            sb.append("this."); //$NON-NLS-1$
            sb.append(property);
            sb.append(" = "); //$NON-NLS-1$
            sb.append(property);
            sb.append(';');
            method.addBodyLine(sb.toString());
        }

        return method;
    }

    protected void addDefaultConstructor(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(topLevelClass.getType().getShortName());
        method.addBodyLine("super();");
        topLevelClass.addMethod(method);
    }
}

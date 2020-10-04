package com.lagou.sqlSession;

import com.lagou.config.BoundSql;
import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import com.lagou.utils.GenericTokenParser;
import com.lagou.utils.ParameterMapping;
import com.lagou.utils.ParameterMappingTokenHandler;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {

    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IntrospectionException, InstantiationException, InvocationTargetException {
        //JDBC代码
        //1.注册驱动，获得连接
        Connection connection = configuration.getDataSource().getConnection();

        //2.转换sql #{} --- ? 并且将#{}中的名字保存下来
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        //3.获取prepareStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        //4.设置参数
            //获取参数对象的类型全路径
        String parameterType = mappedStatement.getParameterType();
        Class<?> aClass = getClassType(parameterType);

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            String content = parameterMapping.getContent();

            //利用反射获取参数对象的属性值
            Field field = aClass.getDeclaredField(content);
            field.setAccessible(true);
            Object o = field.get(params[0]);

            //prepareStatement设置参数
            preparedStatement.setObject(i+1,o);
        }

        //5.执行sql
        ResultSet resultSet = preparedStatement.executeQuery();

        //6.封装返回结果集
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);

        ArrayList<Object> objects = new ArrayList<>();
        while(resultSet.next()){

            Object o = resultTypeClass.newInstance();
            //元数据
            ResultSetMetaData metaData = resultSet.getMetaData();

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                //字段名
                String columnName = metaData.getColumnName(i);
                //字段的值
                Object value = resultSet.getObject(columnName);

                //反射内省创建对象并且属性赋值
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o,value);
            }

            objects.add(o);
        }


        return (List<E>) objects;
    }

    @Override
    public int update(Configuration configuration, MappedStatement mappedStatement, Object user) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Connection connection = configuration.getDataSource().getConnection();

        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        String parameterType = mappedStatement.getParameterType();
        Class<?> aClass = Class.forName(parameterType);

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        for(int i = 0; i < parameterMappings.size();i++){
            ParameterMapping parameterMapping = parameterMappings.get(i);
            String content = parameterMapping.getContent();

            Field declaredField = aClass.getDeclaredField(content);
            declaredField.setAccessible(true);
            Object o = declaredField.get(user);

            preparedStatement.setObject(i+1,o);
        }

        int ret = preparedStatement.executeUpdate();

        return ret;
    }


    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {

        if (parameterType != null) {
            Class<?> aClass = Class.forName(parameterType);
            return aClass;
        }

        return null;
    }

    /**
     * 解析sql：1.将#{}转换为?  2.将#{}中的属性名储存下来
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        //标记解析类，辅助标记解析器完成解析sql
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        //标记解析器
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        String sqlText = genericTokenParser.parse(sql);
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();
        return new BoundSql(sqlText, parameterMappings);
    }
}

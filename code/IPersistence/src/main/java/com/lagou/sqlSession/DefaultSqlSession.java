package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public <E> List<E> selectList(String statementid, Object... params) throws SQLException, IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, InvocationTargetException, ClassNotFoundException {
        //调用simpleExecutor中的query方法来获得结果
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        List<Object> list = simpleExecutor.query(configuration, mappedStatement, params);

        return (List<E>)list;
    }

    @Override
    public <T> T selectOne(String statementid, Object... params) throws SQLException, IllegalAccessException, IntrospectionException, InstantiationException, ClassNotFoundException, InvocationTargetException, NoSuchFieldException {

        List<Object> objects = selectList(statementid,params);
        if(objects.size() == 1){
            return (T)objects.get(0);
        }else{
            throw new RuntimeException("查询为空或者查询结果为多条");
        }

    }

    /**
     * 插入一个user
     * @param statementid
     * @param user
     */
    @Override
    public int updateUser(String statementid, Object user) throws ClassNotFoundException, SQLException, IllegalAccessException, NoSuchFieldException {
        int ret = 0;
        if(user != null){
            SimpleExecutor simpleExecutor = new SimpleExecutor();
            MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
            ret = simpleExecutor.update(configuration,mappedStatement,user);
        }else{
            throw new RuntimeException("用户不能为null");
        }

        return ret;
    }



    @Override
    public <T> T getMapper(Class<?> mapperClass) {

        Object proxy = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //根据不同情况调用selectList或者selectOne
                //1.准备参数，statementid sql语句的唯一标识，无法从配置文件直接获取，可以按照规则限定statementid
                // 使得namespace.id == 接口全限定名.方法名
                String methodName = method.getName();
                String calssName = method.getDeclaringClass().getName();
                String statementid = calssName + "." + methodName;
                //2.准备参数2 params:args

                //获取当前被调用方法的返回值
                Type genericReturnType = method.getGenericReturnType();
                //判断返回值是否有泛型类型参数化
                if(genericReturnType instanceof ParameterizedType){
                    List<Object> objects = selectList(statementid, args);

                    return objects;
                }else{
                    Class<?> returnType = method.getReturnType();
                    if(returnType.getName().equals("int")){
                        return updateUser(statementid,args[0]);
                    }else{
                        return selectOne(statementid,args);
                    }
                }
            }
        });
        return (T) proxy;
    }
}

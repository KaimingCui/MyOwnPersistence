package com.lagou.sqlSession;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface SqlSession {

    /**
     * 查询所有或者按照条件查询多个
     * @param statementid
     * @param params
     * @param <E>
     * @return
     */
    public <E> List<E> selectList(String statementid, Object... params) throws SQLException, IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, InvocationTargetException, ClassNotFoundException;

    /**
     * 按照条件查询一个
     *
     * @param statementid
     * @param params
     * @param <T>
     * @return
     */
    public <T> T selectOne(String statementid, Object... params) throws SQLException, IllegalAccessException, IntrospectionException, InstantiationException, ClassNotFoundException, InvocationTargetException, NoSuchFieldException;


    /**
     * 插入 更新 删除
     * @param statementid
     * @param user
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public int updateUser(String statementid, Object user) throws ClassNotFoundException, SQLException, IllegalAccessException, NoSuchFieldException;

    /**
     * 生成目标接口的代理对象并返回
     * @param mapperClass
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<?> mapperClass);
}

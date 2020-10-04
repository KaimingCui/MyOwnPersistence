package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface Executor {
    /**
     * 执行JDBC代码
     *
     * @param configuration
     * @param mappedStatement
     * @param params
     * @param <E>
     * @return
     */
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IntrospectionException, InstantiationException, InvocationTargetException;

    public int update(Configuration configuration,MappedStatement mappedStatement,Object user) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException;
}

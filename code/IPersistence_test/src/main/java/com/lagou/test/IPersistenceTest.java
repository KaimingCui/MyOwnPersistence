package com.lagou.test;

import com.lagou.dao.IUserDao;
import com.lagou.io.Resources;
import com.lagou.pojo.User;
import com.lagou.sqlSession.SqlSession;
import com.lagou.sqlSession.SqlSessionFactory;
import com.lagou.sqlSession.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class IPersistenceTest {

    @Test
    public void test() throws PropertyVetoException, DocumentException, SQLException, IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, InvocationTargetException, ClassNotFoundException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        User user = new User();
        user.setId(1);
        user.setUsername("lucy");
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        User user2 = userDao.findByCondition(user);
        System.out.println(user2);


        List<User> list = userDao.findAll();
        for (User u : list) {
            System.out.println(u);
        }
    }

    @Test
    public void testInsert() throws PropertyVetoException, DocumentException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        IUserDao iUserDao= sqlSession.getMapper(IUserDao.class);

        User user = new User();
        user.setId(3);
        user.setUsername("kaiming");
        user.setPassword("666");
        user.setBirthday("1995-04-29");
        iUserDao.addUser(user);

    }

    @Test
    public void testUpdate() throws PropertyVetoException, DocumentException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        IUserDao iUserDao= sqlSession.getMapper(IUserDao.class);

        User user = new User();
        user.setId(3);
        user.setUsername("kaimingCui");
        iUserDao.updateUser(user);

    }

    @Test
    public void testDelete() throws PropertyVetoException, DocumentException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        IUserDao iUserDao= sqlSession.getMapper(IUserDao.class);

        User user = new User();
        user.setId(3);
        iUserDao.deleteUser(user);

    }
}

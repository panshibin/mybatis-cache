package com.mybatis.cache;

import com.mybatis.cache.bean.Blog;
import com.mybatis.cache.mapper.BlogMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;

@SpringBootTest
class DemoApplicationTests {


    /**
     * 一级缓存测试
     *
     * @throws IOException
     */
    @Test
    public void select() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream resourceAsStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try{
            BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
            System.out.println("第一次执行："+mapper.selectBlogById(1));
            System.out.println("第二次执行："+mapper.selectBlogById(1));
        }finally{
            sqlSession.close();
        }
    }

    /**
     * 二级缓存测试
     * @throws IOException
     */
    @Test
    public void update() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream resourceAsStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        try {
            BlogMapper mapper1 = sqlSession1.getMapper(BlogMapper.class);
            BlogMapper mapper2 = sqlSession2.getMapper(BlogMapper.class);
            Blog blog = mapper1.selectBlogById(1);
            System.out.println("第一次执行："+blog);
            sqlSession1.commit();

            System.out.println("第二次执行："+mapper2.selectBlogById(1));
        } finally {
            sqlSession1.close();
            sqlSession2.close();
        }
    }

    /**
     * 二级缓存测试多个session会不会缓存查询
     * @throws IOException
     */
    @Test
    public void update2() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream resourceAsStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        SqlSession sqlSession3 = sqlSessionFactory.openSession();
        try {
            BlogMapper mapper1 = sqlSession1.getMapper(BlogMapper.class);
            BlogMapper mapper2 = sqlSession2.getMapper(BlogMapper.class);
            BlogMapper mapper3 = sqlSession3.getMapper(BlogMapper.class);
            Blog blog = mapper1.selectBlogById(1);
            System.out.println("第一次执行："+blog);
            sqlSession1.commit();

            blog.setName("xxxx");
            System.out.println("第二次执行修改："+mapper2.updateByPrimaryKey(blog));
            sqlSession2.commit();

            System.out.println("第三次执行："+mapper3.selectBlogById(1));
        } finally {
            sqlSession1.close();
            sqlSession2.close();
            sqlSession3.close();
        }
    }
}

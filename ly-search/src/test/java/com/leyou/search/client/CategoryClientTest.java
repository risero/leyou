package com.leyou.search.client;

import com.leyou.item.pojo.Category;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CategoryClientTest {

    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void queryByIds() {
        List<Category> categories = categoryClient.queryByIds(Arrays.asList(1L, 2L, 3L));
        // 断言，判断有3个，categories获取的长度也是3个
        Assert.assertEquals(3,categories.size());
        for (Category category : categories) {
            System.out.println(category);
        }
    }
}
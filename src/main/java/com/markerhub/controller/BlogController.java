package com.markerhub.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markerhub.common.lang.Result;
import com.markerhub.entity.Blog;
import com.markerhub.service.BlogService;
import com.markerhub.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 关注公众号：MarkerHub
 * @since 2021-04-28
 */
@RestController
public class BlogController {

    @Autowired
    BlogService blogService;

    @GetMapping("/blogs")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage) {
        Page page = new Page(currentPage, 5);
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().orderByDesc("created"));
        return Result.succ(pageData);
    }

    @GetMapping("/blog/{id}")
    public Result detail(@PathVariable(name = "id") Long id) {
        Blog blog = blogService.getById(id);
        Assert.notNull(blog, "该博客已被删除");
        return Result.succ(blog);
    }

    @RequiresAuthentication
    @GetMapping("/blog/edit")
    public Result edit(@Validated @RequestBody Blog blog) {

        Blog tmp = null;
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        if (blog.getId() != null) { // 编辑
            tmp = blogService.getById(blog.getId());
            // 只能编辑自己的文章
            Assert.isTrue(tmp.getUserId().longValue() == accountProfile.getId().longValue(), "你没有权限编辑");

        } else { // 添加
            tmp = new Blog();
            tmp.setUserId(blog.getUserId());
            tmp.setCreated(LocalDateTime.now());
            tmp.setStatus(0);
//            tmp.setTitle(blog.getTitle());
//            tmp.setDescription(blog.getDescription());

        }

        BeanUtils.copyProperties(blog, tmp, "id", "userId", "created", "status");
        blogService.saveOrUpdate(tmp);

        return Result.succ(blog);
    }

}

package com.lwq.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwq.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    //扩展自己的方法，要是删除的时候有菜品就不能删除
    public void remove(Long id);
}

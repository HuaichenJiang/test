package com.jhc.vo;

import javax.persistence.Entity;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/12/21  16:45
 */
@Entity
public class SizeVariantProduct extends Product {
    private String size;
    private String color;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

package com.jhc.vo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/12/21  15:59
 */
@Entity
public class Street extends BaseEntity {

    @Id
    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

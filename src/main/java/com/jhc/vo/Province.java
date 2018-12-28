package com.jhc.vo;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/15  15:29
 */
@Entity
public class Province extends BaseEntity {
    @Id
    private String code;
    private String name;
    @OneToMany(fetch= FetchType.LAZY,cascade=CascadeType.PERSIST,mappedBy = "province")
    private List<District> districtList;

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

    public List<District> getDistrictList() {
        return districtList;
    }

    public void setDistrictList(List<District> districtList) {
        this.districtList = districtList;
    }
}

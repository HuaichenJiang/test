package com.jhc.vo;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/15  15:31
 */
@Entity
public class District extends BaseEntity {
    @Id
    private String code;
    private String name;
    @ManyToOne
    private Province province;
    private Integer selectNum;
    private String longitude;
    private String latitude;
    @OneToMany(fetch= FetchType.LAZY,cascade=CascadeType.PERSIST,mappedBy = "district")
    private List<City> cityList;


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

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public Integer getSelectNum() {
        return selectNum;
    }

    public void setSelectNum(Integer selectNum) {
        this.selectNum = selectNum;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }
}

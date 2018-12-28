package com.jhc.vo;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/15  15:32
 */
@Entity
public class City extends BaseEntity{
    @Id
    private String code;
    private String name;
    @ManyToOne
    private District district;
    private Integer selectNum;
    private String longitude;
    private String latitude;

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

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
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

}

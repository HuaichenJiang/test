package com.jhc.vo;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/15  10:35
 */
@Entity
public class SysRole extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String role;
    private String discription;
    private Boolean available = Boolean.FALSE;

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="SysRolePermission",joinColumns={@JoinColumn(name="roleId")},inverseJoinColumns={@JoinColumn(name="permissionId")})
    private List<SysPermission> permissions;// 权限 - 角色关系定义;
    @ManyToMany
    @JoinTable(name="SysUserRole",joinColumns={@JoinColumn(name="roleId")},inverseJoinColumns={@JoinColumn(name="uid")})
    private List<Users> userInfos;// 一个角色对应多个用户

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public List<SysPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<SysPermission> permissions) {
        this.permissions = permissions;
    }

    public List<Users> getUserInfos() {
        return userInfos;
    }

    public void setUserInfos(List<Users> userInfos) {
        this.userInfos = userInfos;
    }
}

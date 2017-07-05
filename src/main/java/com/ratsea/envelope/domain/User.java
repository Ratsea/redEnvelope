package com.ratsea.envelope.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Ratsea on 2017/7/5.
 */
public class User implements Serializable {

    private Long id;

    private String nickName;

    private BigDecimal money;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickName='" + nickName + '\'' +
                ", money=" + money +
                '}';
    }
}

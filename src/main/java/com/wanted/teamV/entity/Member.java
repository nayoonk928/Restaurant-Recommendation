package com.wanted.teamV.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String account;

    @Column(nullable = false)
    String password;

    Double lat;

    Double lon;

    @Column(nullable = false)
    Boolean recommend = false;

    @Builder
    public Member(String account, String password, Double lat, Double lon) {
        this.account = account;
        this.password = password;
        this.lat = lat;
        this.lon = lon;
    }

    public Member(String account, String password, Double lat, Double lon, Boolean recommend) {
        this.account = account;
        this.password = password;
        this.lat = lat;
        this.lon = lon;
        this.recommend = recommend;
    }
}

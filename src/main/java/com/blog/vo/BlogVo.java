package com.blog.vo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "blog_info")
public class BlogVo {

    @Id
    @Column(nullable = false)
    private String blogName;

    @Column(nullable = false)
    private Integer blogSearchCount;

}

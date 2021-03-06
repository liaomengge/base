package com.github.liaomengge.service.base_framework.mongo.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by liaomengge on 17/3/2.
 */
@Getter
@Setter
public class BaseMongoDoc implements Serializable {

    private static final long serialVersionUID = -5478100873243643757L;

    @Id
    private String _id;
}

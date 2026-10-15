package com.hikari.rent.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// java example
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

  @Override
  public void insertFill(MetaObject metaObject) {
    LocalDateTime now = LocalDateTime.now();

    strictInsertFill(
      metaObject, "createTime", LocalDateTime.class, now
    );
    strictInsertFill(
      metaObject, "updateTime", LocalDateTime.class, now
    );
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    // 覆盖从数据库读取出来的旧更新时间
    setFieldValByName(
      "updateTime", LocalDateTime.now(), metaObject
    );
  }
}

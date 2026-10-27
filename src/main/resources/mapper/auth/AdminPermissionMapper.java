<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "https://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.hikari.rent.auth.mapper.AdminPermissionMapper">

  <resultMap
    id="PermissionResultMap"
    type="com.hikari.rent.auth.repository.model.AdminPermissionRow">

    <id column="id" property="id"/>

    <result column="code" property="code"/>
    <result column="name" property="name"/>
    <result column="module" property="module"/>
    <result column="description" property="description"/>

  </resultMap>

  <resultMap
    id="RoleResultMap"
    type="com.hikari.rent.auth.repository.model.AdminRoleRow">

    <id column="id" property="id"/>

    <result column="code" property="code"/>
    <result column="name" property="name"/>
    <result column="description" property="description"/>
    <result column="scope" property="scope"/>
    <result column="status" property="status"/>

  </resultMap>

  <select
    id="selectAllPermissions"
    resultMap="PermissionResultMap">

    SELECT
    id,
    code,
    name,
    module,
    description

    FROM permissions

    ORDER BY module ASC, id ASC
  </select>

  <!-- Keep permission definitions stable during assignment. -->
  <select
    id="selectAllPermissionsForShare"
    resultMap="PermissionResultMap">

    SELECT
    id,
    code,
    name,
    module,
    description

    FROM permissions

    ORDER BY module ASC, id ASC

    FOR SHARE
  </select>

  <select id="selectRoleById" resultMap="RoleResultMap">
    SELECT
    id,
    code,
    name,
    description,
    scope,
    status

    FROM roles

    WHERE id = #{roleId}
  </select>

  <!-- Serialize permission replacement for the same role. -->
  <select
    id="selectRoleByIdForUpdate"
    resultMap="RoleResultMap">

    SELECT
    id,
    code,
    name,
    description,
    scope,
    status

    FROM roles

    WHERE id = #{roleId}

    FOR UPDATE
  </select>

  <select
    id="selectRolePermissionIds"
    resultType="java.lang.Long">

    SELECT permission_id
    FROM role_permissions
    WHERE role_id = #{roleId}
    ORDER BY permission_id
  </select>

  <delete id="deleteRolePermissions">
    DELETE FROM role_permissions
    WHERE role_id = #{roleId}
  </delete>

  <!-- The repository skips this statement for an empty list. -->
  <insert id="insertRolePermissions">
    INSERT INTO role_permissions (
    role_id,
    permission_id,
    created_at
    )
    VALUES
    <foreach
      collection="permissionIds"
      item="permissionId"
      separator=",">
      (
      #{roleId},
      #{permissionId},
      UTC_TIMESTAMP(3)
      )
    </foreach>
  </insert>

</mapper>

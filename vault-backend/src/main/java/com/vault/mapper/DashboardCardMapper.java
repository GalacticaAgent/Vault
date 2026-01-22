package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.DashboardCard;
import org.apache.ibatis.annotations.Mapper;

/**
 * 自定义卡片Mapper接口
 */
@Mapper
public interface DashboardCardMapper extends BaseMapper<DashboardCard> {
}

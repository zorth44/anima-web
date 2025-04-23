package com.zorth.anima_web.repository;

import org.apache.ibatis.annotations.*;

import com.zorth.anima_web.model.entity.SyncHistory;

@Mapper
public interface SyncHistoryRepository {
    
    @Insert("INSERT INTO sync_history (sync_time, sync_type, affected_records, status, error_message, " +
            "created_at, sync_status, current_page, total_pages, last_successful_page) " +
            "VALUES (#{syncTime}, #{syncType}, #{affectedRecords}, #{status}, #{errorMessage}, " +
            "#{createdAt}, #{syncStatus}, #{currentPage}, #{totalPages}, #{lastSuccessfulPage})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SyncHistory syncHistory);
    
    @Update("UPDATE sync_history SET sync_time = #{syncTime}, sync_type = #{syncType}, " +
            "affected_records = #{affectedRecords}, status = #{status}, error_message = #{errorMessage}, " +
            "sync_status = #{syncStatus}, current_page = #{currentPage}, total_pages = #{totalPages}, " +
            "last_successful_page = #{lastSuccessfulPage} WHERE id = #{id}")
    void update(SyncHistory syncHistory);
    
    @Select("SELECT * FROM sync_history WHERE sync_status = #{syncStatus} ORDER BY created_at DESC LIMIT 1")
    SyncHistory findFirstBySyncStatusOrderByCreatedAtDesc(String syncStatus);
} 
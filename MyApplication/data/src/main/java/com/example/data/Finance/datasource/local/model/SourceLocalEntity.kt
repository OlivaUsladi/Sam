package com.example.data.Finance.datasource.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

 //pendingCreate - запись создана локально и ещё не отправлена на сервер;
 //pendingUpdate - запись существует на сервере, но локально была изменена и ждёт отправки;
 //pendingDelete - запись удалена локально, но удаление ещё не подтверждено сервером
 // (она исключается из выдачи, но остаётся в БД до завершения sync).
@Entity(tableName = "fin_sources")
data class SourceLocalEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val name: String,
    val type: String,
    val pendingCreate: Boolean = false,
    val pendingUpdate: Boolean = false,
    val pendingDelete: Boolean = false,
)

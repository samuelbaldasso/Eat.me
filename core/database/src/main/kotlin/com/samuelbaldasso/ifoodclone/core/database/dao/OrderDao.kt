package com.samuelbaldasso.ifoodclone.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.samuelbaldasso.ifoodclone.core.database.entity.OrderEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OrderItemEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OrderItemOptionEntity
import com.samuelbaldasso.ifoodclone.core.database.relation.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Transaction
    @Query("SELECT * FROM orders ORDER BY createdAtMillis DESC")
    fun getOrders(): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderById(orderId: String): Flow<OrderWithItems?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItemOptions(options: List<OrderItemOptionEntity>)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    @Transaction
    suspend fun saveCompleteOrder(
        order: OrderEntity,
        items: List<OrderItemEntity>,
        options: List<OrderItemOptionEntity>
    ) {
        insertOrder(order)
        if (items.isNotEmpty()) {
            insertOrderItems(items)
        }
        if (options.isNotEmpty()) {
            insertOrderItemOptions(options)
        }
    }
}

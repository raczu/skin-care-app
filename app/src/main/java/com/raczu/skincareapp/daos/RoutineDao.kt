package com.raczu.skincareapp.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.raczu.skincareapp.entities.Product
import com.raczu.skincareapp.entities.Routine
import com.raczu.skincareapp.entities.RoutineProductCrossRef
import com.raczu.skincareapp.entities.RoutineWithProducts
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Insert
    suspend fun insert(routine: Routine): Long

    @Insert
    suspend fun insert(routineProductCrossRef: RoutineProductCrossRef)

    @Transaction
    suspend fun insertWithProducts(routine: Routine, products: List<Product>) {
        val routineId = insert(routine).toInt()
        products.forEach { product ->
            insert(RoutineProductCrossRef(routineId, product.productId))
        }
    }

    @Update
    suspend fun update(routine: Routine)

    @Delete
    suspend fun delete(routine: Routine)

    @Transaction
    @Query("SELECT * FROM routine WHERE routine_id = :id")
    fun getRoutineWithProducts(id: Int): Flow<RoutineWithProducts?>

    @Query("SELECT * FROM routine ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    fun getRoutinesWithCursor(limit: Int = 15, offset: Int = 0): Flow<List<Routine>>

    @Query("""
        SELECT * FROM routine
        WHERE STRFTIME('%Y-%m-%d', DATETIME(created_at / 1000, 'unixepoch')) = 
        STRFTIME('%Y-%m-%d', DATETIME('now'))
    """)
    fun getTodayRoutines(): Flow<List<Routine>>

    @Query("""
        SELECT * FROM routine
        WHERE STRFTIME('%Y-%m-%d', DATETIME(created_at / 1000, 'unixepoch')) = 
        STRFTIME('%Y-%m-%d', DATETIME(:timestamp / 1000, 'unixepoch'))
    """)
    fun getGivenDateRoutines(timestamp: Long): Flow<List<Routine>>
}
package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.local.daos.RoutineDao
import com.raczu.skincareapp.data.local.entities.Product
import com.raczu.skincareapp.data.local.entities.Routine
import com.raczu.skincareapp.data.local.entities.RoutineWithProducts
import kotlinx.coroutines.flow.Flow

class RoutineRepositoryOld(private val routineDao: RoutineDao) {
    suspend fun insert(routine: Routine) = routineDao.insert(routine)

    suspend fun insertWithProducts(routine: Routine, products: List<Product>) {
        routineDao.insertWithProducts(routine, products)
    }

    suspend fun update(routine: Routine) = routineDao.update(routine)

    suspend fun delete(routine: Routine) = routineDao.delete(routine)

    fun getRoutineWithProducts(id: Int): Flow<RoutineWithProducts?> {
        return routineDao.getRoutineWithProducts(id)
    }

    fun getRoutinesWithCursor(limit: Int = 15, offset: Int = 0): Flow<List<Routine>> {
        return routineDao.getRoutinesWithCursor(limit, offset)
    }

    fun getTodayRoutines(): Flow<List<Routine>> = routineDao.getTodayRoutines()

    fun getGivenDateRoutines(timestamp: Long): Flow<List<Routine>> {
        return routineDao.getGivenDateRoutines(timestamp)
    }
}
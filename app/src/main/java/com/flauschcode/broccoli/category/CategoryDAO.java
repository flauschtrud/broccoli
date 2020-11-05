package com.flauschcode.broccoli.category;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDAO {

    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories ORDER BY name")
    LiveData<List<Category>> findAll();

    @Query("SELECT * FROM categories WHERE name LIKE :name")
    Category searchByName(String name);
}

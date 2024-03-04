package com.assignment.todo.dal.dao;

import com.assignment.todo.dal.entity.TodoItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoItemEntityRepository extends JpaRepository<TodoItemEntity, Integer> {

}

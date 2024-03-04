package com.assignment.todo.dal.dao;

import com.assignment.todo.dal.entity.TodoItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoItemEntityRepository extends JpaRepository<TodoItemEntity, Integer> {

    List<TodoItemEntity> findAllByStatusAndDueDateTimeLessThan(String status, LocalDateTime dueDateTime);

    List<TodoItemEntity> findAllByStatusNot(String status);

}

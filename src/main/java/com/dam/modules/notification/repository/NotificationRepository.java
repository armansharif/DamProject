package com.dam.modules.notification.repository;

import com.dam.modules.notification.model.Notification;
import com.dam.modules.user.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findAllByUsers(Users user);
}

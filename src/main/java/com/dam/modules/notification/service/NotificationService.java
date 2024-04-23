package com.dam.modules.notification.service;


import com.dam.modules.notification.model.Notification;
import com.dam.modules.notification.repository.NotificationRepository;
import com.dam.modules.user.model.Users;
import com.dam.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private NotificationRepository notificationRepository;
    private UserService userService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    public List<Notification> getNotificationOfUser(Users user, int justUnread) {
        if (justUnread == 1)
            return notificationRepository.findAllByUsersOrderByIdDesc(user).stream().filter(notification -> notification.getIsRead() == 0).collect(Collectors.toList());
        else
            return notificationRepository.findAllByUsersOrderByIdDesc(user);
    }

    public Optional<Notification> findNotification(Long id) {
        return notificationRepository.findById(id);
    }

    public Notification setReadNotifications(Notification notification) {
        notification.setIsRead(1);
        notificationRepository.save(notification);
        return notification;
    }

    public Notification saveNotification(String message,
                                         String route,
                                         int hasAction,
                                         String actionTitle,
                                         String actionRoute,
                                         Users user ,
                                         int isRead,
                                         int readOnly,
                                         Long dam_id,
                                         int priority) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setHasAction(hasAction);
        notification.setActionTitle(actionTitle);
        notification.setActionRoute(actionRoute);
        notification.setRoute(route);
        notification.setUsers(user);
        notification.setIsRead(isRead);
        notification.setReadOnly(readOnly);
        notification.setDamId(dam_id.intValue());
        notification.setPriority(priority);
        return notificationRepository.save(notification);
    }
}

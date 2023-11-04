package com.dam.modules.notification;

import com.dam.commons.Routes;
import com.dam.modules.notification.model.Notification;
import com.dam.modules.notification.repository.NotificationRepository;
import com.dam.modules.notification.service.NotificationService;
import com.dam.modules.user.controller.UserController;
import com.dam.modules.user.model.Users;
import com.dam.modules.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping(produces = "application/json")
public class NotificationController {
    Logger logger = LoggerFactory.getLogger(UserController.class);


    private NotificationService notificationService;
    private UserService userService;

    @Autowired
    public NotificationController(NotificationRepository notificationRepository, NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping(value = {Routes.GET_notifications})
    public ResponseEntity<Object> getNotificationByToken(HttpServletRequest request, @RequestParam(required = false, defaultValue = "1") int unread) {
        Long user_id = userService.getUserIdByToken(request);

        if (user_id == null)
            return ResponseEntity.notFound().build();
        Users user = userService.findUser(user_id).orElse(null);
        if (user != null) {
            return ResponseEntity.ok()
                    .body(notificationService.getNotificationOfUser(user, unread));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = {Routes.POST_notification_read})
    public ResponseEntity<Object> setNotificationRead(HttpServletRequest request, @PathVariable(value = "id") Long id) {
        Optional<Notification> notification = notificationService.findNotification(id);
        if (id == null)
            return ResponseEntity.notFound().build();
        if (notification.isPresent()) {
            notificationService.setReadNotifications(notification.get());
            return ResponseEntity.ok()
                    .body(notification);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

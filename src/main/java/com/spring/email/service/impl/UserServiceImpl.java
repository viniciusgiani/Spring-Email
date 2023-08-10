package com.spring.email.service.impl;

import com.spring.email.exceptions.EmailAlreadyExistsException;
import com.spring.email.model.Confirmation;
import com.spring.email.model.User;
import com.spring.email.repository.ConfirmationRepository;
import com.spring.email.repository.UserRepository;
import com.spring.email.service.EmailService;
import com.spring.email.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ConfirmationRepository confirmationRepository;

    private final EmailService emailService;

    @Override
    public User saveUser(User userParameter) {
        if (userRepository.existsByEmail(userParameter.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        } else {
            User user = new User();
            user.setEnabled(false);
            user = userRepository.save(userParameter);

            Confirmation confirmation = new Confirmation(user);
            confirmationRepository.save(confirmation);

//            emailService.sendSimpleMailMessage(user.getName(), user.getEmail(), confirmation.getToken());
//
//            emailService.sendMimeMessageWithAttachments(user.getName(), user.getEmail(), confirmation.getToken());

//            emailService.sendMimeMessageWithEmbeddedFiles(user.getName(), user.getEmail(), confirmation.getToken());

//            emailService.sendHtmlEmail(user.getName(), user.getEmail(), confirmation.getToken());

            emailService.sendHtmlEmailsWithEmbeddedFiles(user.getName(), user.getEmail(), confirmation.getToken());

            return user;
        }
    }

    @Override
    public Boolean verifyToken(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        User user = userRepository.findByEmailIgnoreCase(confirmation.getUser().getEmail());
        user.setEnabled(true);
        userRepository.save(user);
        return Boolean.TRUE;
    }
}

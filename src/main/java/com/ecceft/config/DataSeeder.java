package com.ecceft.config;

import com.ecceft.dto.RegisterRequest;
import com.ecceft.repository.AccountRepository;
import com.ecceft.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder
        implements CommandLineRunner {

    private final AuthService authService;

    private final AccountRepository accountRepository;

    @Override
    public void run(String... args)
            throws Exception {

        if (!accountRepository.existsByUsername(
                "ngozi.blessing"
        )) {

            RegisterRequest r1 =
                    new RegisterRequest();

            r1.setFullName(
                    "Udegbuna Ngozi Blessing"
            );

            r1.setEmail(
                    "ngozi@example.com"
            );

            r1.setUsername(
                    "ngozi.blessing"
            );

            r1.setPassword(
                    "Test@1234"
            );

            authService.register(r1);
        }

        if (!accountRepository.existsByUsername(
                "john.doe"
        )) {

            RegisterRequest r2 =
                    new RegisterRequest();

            r2.setFullName(
                    "John Doe"
            );

            r2.setEmail(
                    "john@example.com"
            );

            r2.setUsername(
                    "john.doe"
            );

            r2.setPassword(
                    "Test@1234"
            );

            authService.register(r2);
        }
    }
}
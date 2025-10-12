package com.example.parking_management.service;


import com.example.parking_management.dto.authDTO.AuthResponse;
import com.example.parking_management.dto.authDTO.LoginRequest;
import com.example.parking_management.dto.userDTO.RegisterRequest;
import com.example.parking_management.jwt.JwtService;


import com.example.parking_management.model.User;
import com.example.parking_management.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service

public class AuthService {

    @Value("${spring.mail.username}")
    private String mail;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    // envio de correos automatico
    private final JavaMailSender mailSender;
    private SimpleMailMessage templateMessage;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, JavaMailSender mailSender, SimpleMailMessage templateMessage) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.mailSender = mailSender;
        this.templateMessage = templateMessage;
    }

    public AuthResponse register(RegisterRequest request) {
        // Verificar si el email ya existe
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Verificar si la identificación ya existe
        if (userRepository.findById(request.getIdCard()).isPresent()) {
            throw new RuntimeException("La identificación ya está registrada");
        }

        // Crear el usuario
        User user = new User();
        user.setIdCard(request.getIdCard());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar contraseña
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDirection(request.getDirection());
        user.setRole(request.getRole());
        user.setRegistrationDate(LocalDateTime.now());
        user.setActive(true);

        userRepository.save(user);

        //Envio del corrreo de confirmacion al usuario que envia el mensaje al propietario del sitio
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(user.getEmail());
        msg.setSubject("¡Bienvenido al sistema de Parqueadero! "); // Asunto más claro
        msg.setText(
                "Hola " + user.getName() + ",\n\n" +
                        "Tu registro en el sistema de parqueadero se ha realizado con éxito.\n\n" +
                        " Fecha de registro: " + user.getRegistrationDate() + "\n" +
                        " Usuario (email): " + user.getEmail() + "\n\n" +
                        "Ya puedes ingresar a la plataforma con tus credenciales.\n\n" +
                        "Si tienes dudas, comunícate con nosotros a través de nuestro WhatsApp empresarial: https://wa.me/573103212753\n\n" +
                        "¡Gracias por confiar en nuestro servicio!\n\n" +
                        "Atentamente,\n" +
                        "Equipo de Parqueadero"
        );

        try {
            this.mailSender.send(msg);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }

        // Generar token JWT
        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .message("Usuario registrado exitosamente")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Si llega aquí, la autenticación fue exitosa
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar token JWT
        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .message("Login exitoso")
                .build();
    }
}


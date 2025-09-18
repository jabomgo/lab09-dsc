package br.ufpb.dcx.dsc.repositorios.controller;

import br.ufpb.dcx.dsc.repositorios.dto.ChangePasswordRequestDTO;
import br.ufpb.dcx.dsc.repositorios.dto.LoginRequestDTO;
import br.ufpb.dcx.dsc.repositorios.dto.LoginResponseDTO;
import br.ufpb.dcx.dsc.repositorios.services.JwtService;
import br.ufpb.dcx.dsc.repositorios.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        org.springframework.security.core.userdetails.User userDetails =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);
        return new LoginResponseDTO(token);
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        br.ufpb.dcx.dsc.repositorios.models.User dbUser = userRepository.findByNome(currentUsername).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getOldPassword(), dbUser.getPassword())) {
            throw new RuntimeException("Senha antiga incorreta");
        }

        dbUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(dbUser);

        return "Senha alterada com sucesso!";
    }
}

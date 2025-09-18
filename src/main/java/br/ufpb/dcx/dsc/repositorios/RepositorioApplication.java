package br.ufpb.dcx.dsc.repositorios;

import br.ufpb.dcx.dsc.repositorios.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class RepositorioApplication {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByNome("admin").isEmpty()) {
                br.ufpb.dcx.dsc.repositorios.models.User admin = new br.ufpb.dcx.dsc.repositorios.models.User();
                admin.setNome("admin");
                admin.setPassword(encoder.encode("123456"));
                userRepository.save(admin);
                System.out.println(">>> Usuário admin criado com senha 123456");
            }
        };
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    public static void main(String[] args) {
        SpringApplication.run(RepositorioApplication.class, args);
    }

}

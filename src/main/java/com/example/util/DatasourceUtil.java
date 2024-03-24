package com.example.util;

import com.example.entity.Role;
import com.example.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DatasourceUtil {
    @Autowired
    RoleRepository roleRepository;

//    @Bean
    ApplicationRunner roleDataInflate() {
        return args -> {
            roleRepository.save(Role.guest());
            roleRepository.save(Role.admin());
            roleRepository.save(Role.user());
            roleRepository.save(Role.vip());
        };
    }


}

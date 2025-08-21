package com.ynm.researchpaperservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ynm.researchpaperservice.dto.UserDto;
import java.io.IOException;

@Configuration
public class JacksonConfig {
    @Bean
    public Module securityModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(UserDetails.class, new UserDetailsDeserializer());
        return module;
    }

    static class UserDetailsDeserializer extends JsonDeserializer<UserDetails> {
        @Override
        public UserDetails deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return ctxt.readValue(p, UserDto.class);
        }
    }
}

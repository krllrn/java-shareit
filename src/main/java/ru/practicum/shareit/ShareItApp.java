package ru.practicum.shareit;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShareItApp {

	public static final String USER_ID_HEADER_REQUEST = "X-Sharer-User-Id";

	public static void main(String[] args) {
		SpringApplication.run(ShareItApp.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}

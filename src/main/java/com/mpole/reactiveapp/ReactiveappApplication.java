package com.mpole.reactiveapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
//@ComponentScans({
//		@ComponentScan("com.mpole.imp_framework")
//})
public class ReactiveappApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveappApplication.class, args);
	}

}

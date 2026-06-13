package com.project.youtlix;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.assertj.core.api.Assertions.assertThat;

class YoutlixApplicationTests {

    @Test
    void applicationClassIsSpringBootEntryPoint() {
        assertThat(YoutlixApplication.class.getAnnotation(SpringBootApplication.class)).isNotNull();
    }
}

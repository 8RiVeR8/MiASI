package com.project.youtlix.unit;

import com.project.youtlix.YoutlixApplication;
import com.project.youtlix.testsupport.annotation.UnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class YoutlixApplicationUnitTest {

    @Test
    void applicationClassIsSpringBootEntryPoint() {
        assertThat(YoutlixApplication.class.getAnnotation(SpringBootApplication.class)).isNotNull();
    }
}

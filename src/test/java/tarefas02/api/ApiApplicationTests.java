package tarefas02.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiApplicationTests {

    @Test
    void applicationClassHasSpringBootConfiguration() {
        assertTrue(ApiApplication.class.isAnnotationPresent(SpringBootApplication.class));
    }
}

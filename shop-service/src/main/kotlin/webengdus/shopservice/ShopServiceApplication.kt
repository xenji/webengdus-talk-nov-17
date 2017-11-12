package webengdus.shopservice

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@SpringBootApplication
@EnableJpaRepositories
class ShopServiceApplication {
    @Bean
    fun taskExecutor(): TaskExecutor = SimpleAsyncTaskExecutor()
}

fun main(args: Array<String>) {
    SpringApplication.run(ShopServiceApplication::class.java, *args)
}

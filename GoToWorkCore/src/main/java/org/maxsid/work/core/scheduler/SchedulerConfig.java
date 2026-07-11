package org.maxsid.work.core.scheduler;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class SchedulerConfig {
    @Bean
    public LockProvider lockProvider(DataSource dataSource) { //Почитать про lock provider! Можно ли без него обойтись?
        return new JdbcTemplateLockProvider(JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .withTableName("schedlock")  //  Имя таблицы
                .usingDbTime()
                .build()
        );
    }
}

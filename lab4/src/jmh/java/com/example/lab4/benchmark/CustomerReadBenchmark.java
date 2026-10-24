package com.example.lab4.benchmark;

import com.example.lab4.Lab4Application;
import com.example.lab4.entity.Customer;
import com.example.lab4.repository.CustomerRepository;
import com.example.lab4.service.CustomerService;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@Threads(1)
public class CustomerReadBenchmark {
    @State(Scope.Benchmark)
    public static class SpringState {
        private static final String EMAIL = "demo@example.com";

        private ConfigurableApplicationContext context;
        private CustomerService customerService;
        private CustomerRepository customerRepository;

        @Setup(Level.Trial)
        public void startSpring() {
            context = new SpringApplicationBuilder(Lab4Application.class)
                    .web(WebApplicationType.NONE)
                    .run("--app.seed.enabled=false", "--logging.level.root=WARN");
            customerService = context.getBean(CustomerService.class);
            customerRepository = context.getBean(CustomerRepository.class);
            customerService.findByEmail(EMAIL).orElseThrow(
                    () -> new IllegalStateException("Demo customer is missing; start PostgreSQL and run the app once first."));
        }

        @Setup(Level.Iteration)
        public void warmCache() {
            customerService.findByEmail(EMAIL).orElseThrow();
        }

        @TearDown(Level.Trial)
        public void stopSpring() {
            if (context != null) context.close();
        }
    }

    @Benchmark
    public Customer cachedServiceRead(SpringState state) {
        return state.customerService.findByEmail(SpringState.EMAIL).orElseThrow();
    }

    @Benchmark
    public Customer uncachedRepositoryRead(SpringState state) {
        return state.customerRepository.findByEmail(SpringState.EMAIL).orElseThrow();
    }
}

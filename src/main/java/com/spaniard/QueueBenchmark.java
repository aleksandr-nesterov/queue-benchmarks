package com.spaniard;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Control;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

@State(Scope.Group)
public class QueueBenchmark {

    private BlockingQueue<Integer> linkedBlockingQueue;
    private BlockingQueue<Integer> arrayBlockingQueue;
    private Queue<Integer> concurrentLinkedQueue;

    @Setup
    public void setup() {
        linkedBlockingQueue = new LinkedBlockingDeque<>(64 * 1024);
        arrayBlockingQueue = new ArrayBlockingQueue<>(64 * 1024);
        concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
    }

    @Group("arrayBlockingQueue")
    @Benchmark
    public Integer read_arrayBlockingQueue(Control cnt) throws InterruptedException {
        return read(cnt, arrayBlockingQueue);
    }

    @Group("arrayBlockingQueue")
    @Benchmark
    public void write_arrayBlockingQueue(Control cnt) throws InterruptedException {
        write(cnt, arrayBlockingQueue);
    }

    @Group("linkedBlockingQueue")
    @Benchmark
    public Integer read_linkedBlockingQueue(Control cnt) throws InterruptedException {
        return read(cnt, linkedBlockingQueue);
    }

    @Group("linkedBlockingQueue")
    @Benchmark
    public void write_linkedBlockingQueue(Control cnt) throws InterruptedException {
        write(cnt, linkedBlockingQueue);
    }

    @Group("concurrentLinkedQueue")
    @Benchmark
    public Integer read_concurrentLinkedQueue(Control cnt) throws InterruptedException {
        return read(cnt, concurrentLinkedQueue);
    }

    @Group("concurrentLinkedQueue")
    @Benchmark
    public void write_concurrentLinkedQueue(Control cnt) throws InterruptedException {
        write(cnt, concurrentLinkedQueue);
    }

    private void write(Control cnt, Queue<Integer> q) {
        while (!cnt.stopMeasurement && q.offer(23))
            Thread.yield();
    }

    private Integer read(Control cnt, Queue<Integer> q)  {
        Integer v = null;
        while(!cnt.stopMeasurement && (v = q.poll()) == null)
            Thread.yield();

        return v;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(QueueBenchmark.class.getSimpleName())
                .measurementTime(TimeValue.seconds(5))
                .warmupIterations(3)
                .warmupTime(TimeValue.seconds(5))
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}

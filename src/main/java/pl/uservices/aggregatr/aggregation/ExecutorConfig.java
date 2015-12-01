package pl.uservices.aggregatr.aggregation;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.cloud.sleuth.instrument.TraceRunnable;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;

@Configuration
public class ExecutorConfig extends AsyncConfigurerSupport {

	@Autowired
	private BeanFactory beanFactory;

	@Override
	public ScheduledExecutorService getAsyncExecutor() {
		return new LazyTraceExecutor(this.beanFactory, new SimpleAsyncTaskExecutor());
	}


	public static class LazyTraceExecutor implements ScheduledExecutorService {

		private Trace trace;
		private final BeanFactory beanFactory;
		private final Executor delegate;
		private final ScheduledExecutorService scheduledExecutorService;

		public LazyTraceExecutor(BeanFactory beanFactory, Executor delegate) {
			this.beanFactory = beanFactory;
			this.delegate = delegate;
			this.scheduledExecutorService = Executors.newScheduledThreadPool(100);
		}

		@Override
		public void execute(Runnable command) {
			if (this.trace == null) {
				try {
					this.trace = this.beanFactory.getBean(Trace.class);
				}
				catch (NoSuchBeanDefinitionException e) {
					this.delegate.execute(command);
				}
			}
			this.delegate.execute(new TraceRunnable(this.trace, command));
		}

		@Override
		public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
			return scheduledExecutorService.schedule(command, delay, unit);
		}

		@Override
		public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
			return scheduledExecutorService.schedule(callable, delay, unit);
		}

		@Override
		public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
			return scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, period, unit);
		}

		@Override
		public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
			return scheduledExecutorService.scheduleWithFixedDelay(command, initialDelay, delay, unit);
		}

		@Override
		public void shutdown() {
			scheduledExecutorService.shutdown();
		}

		@Override
		public List<Runnable> shutdownNow() {
			return scheduledExecutorService.shutdownNow();
		}

		@Override
		public boolean isShutdown() {
			return scheduledExecutorService.isShutdown();
		}

		@Override
		public boolean isTerminated() {
			return scheduledExecutorService.isTerminated();
		}

		@Override
		public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
			return scheduledExecutorService.awaitTermination(timeout, unit);
		}

		@Override
		public <T> Future<T> submit(Callable<T> task) {
			return scheduledExecutorService.submit(task);
		}

		@Override
		public <T> Future<T> submit(Runnable task, T result) {
			return scheduledExecutorService.submit(task, result);
		}

		@Override
		public Future<?> submit(Runnable task) {
			return scheduledExecutorService.submit(task);
		}

		@Override
		public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
			return scheduledExecutorService.invokeAll(tasks);
		}

		@Override
		public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
			return scheduledExecutorService.invokeAll(tasks, timeout, unit);
		}

		@Override
		public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
			return scheduledExecutorService.invokeAny(tasks);
		}

		@Override
		public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			return scheduledExecutorService.invokeAny(tasks, timeout, unit);
		}
	}
}

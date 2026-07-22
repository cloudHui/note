package timesutils;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TaskList<T extends Task> extends ArrayList<T> {
	private final AtomicLong processor;
	private Date time;
	private final ReadWriteLock lock;

	public TaskList() {
		this(64);
	}

	public TaskList(int initialCapacity) {
		super(initialCapacity);
		this.processor = new AtomicLong(0L);
		this.lock = new ReentrantReadWriteLock(false);
	}

	public boolean isBusy() {
		return this.processor.get() != 0L;
	}

	public boolean isSelf(long processorId) {
		return this.getProcessorId() == processorId;
	}

	public boolean getProcessingAuthority(long processorId) {
		if (this.processor.compareAndSet(0L, processorId)) {
			this.time = new Date();
			return true;
		} else {
			return this.processor.get() == processorId;
		}
	}

	public void releaseProcessingAuthority(long processorId) {
		if (this.processor.compareAndSet(processorId, 0L)) {
			this.time = null;
		}
	}

	public long getProcessorId() {
		return this.processor.get();
	}

	public void updateTime() {
		this.time = new Date();
	}

	public Date getTime() {
		return this.time;
	}

	public boolean add(T task) {
		this.lock.writeLock().lock();

		boolean addSuccess;
		try {
			addSuccess = super.add(task);
		} finally {
			this.lock.writeLock().unlock();
		}

		return addSuccess;
	}

	public T pop() {
		if (this.isEmpty()) {
			return null;
		} else {
			this.lock.writeLock().lock();

			Task task;
			try {
				if (!this.isEmpty()) {
					this.time = new Date();
					task = super.remove(0);
					return (T) task;
				}

				task = null;
			} finally {
				this.lock.writeLock().unlock();
			}

			return (T) task;
		}
	}
}

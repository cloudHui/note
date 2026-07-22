package threadtutil.timer.model;

import threadtutil.thread.Task;

public class SerialTimeNode extends TimeNode
		implements Task {

	private final int groupId;

	public SerialTimeNode(int groupId, int id, Task runner, int delay, int interval) {
		this(groupId, id, runner, interval, delay, -1);
	}

	public SerialTimeNode(int groupId, int id, Task runner, int delay, int interval, int count) {
		super(id, runner, delay, interval, count);
		this.groupId = groupId;
	}

	public int groupId() {
		return groupId;
	}
}

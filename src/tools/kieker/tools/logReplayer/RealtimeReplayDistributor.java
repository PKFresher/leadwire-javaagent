/***************************************************************************
 * Copyright 2012 by
 *  + Christian-Albrechts-University of Kiel
 *    + Department of Computer Science
 *      + Software Engineering Group 
 *  and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.tools.logReplayer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import kieker.analysis.AnalysisController;
import kieker.analysis.exception.MonitoringRecordConsumerException;
import kieker.analysis.plugin.annotation.InputPort;
import kieker.analysis.plugin.annotation.Plugin;
import kieker.analysis.plugin.filter.AbstractFilterPlugin;
import kieker.common.configuration.Configuration;
import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.common.record.IMonitoringRecord;
import kieker.monitoring.timer.ITimeSource;
import kieker.monitoring.timer.SystemNanoTimer;
import kieker.tools.logReplayer.FSReaderRealtime.FSReaderRealtimeCons;

/**
 * IMonitoringRecordConsumerPlugin that distributes the log records to the worker thread for "real time" replays.<br>
 * 
 * This class has exactly one input port named "in" and one output ports named "out".<br>
 * 
 * TODO: Currently this class <b>can not</b> be used for the later analysis tool, as the objects for the constructor cannot be configured with a configuration
 * object.
 * 
 * @author Robert von Massow
 * 
 */
@Plugin
public class RealtimeReplayDistributor extends AbstractFilterPlugin {
	public static final String INPUT_PORT_NAME_MONITORING_RECORDS = "monitoringRecords";

	public static final String CONFIG_PROPERTY_NAME_NUM_WORKERS = "numWorkers";

	private static final Log LOG = LogFactory.getLog(RealtimeReplayDistributor.class);

	private static final ITimeSource TIMESOURCE = SystemNanoTimer.getInstance();
	private static final int QUEUE_SIZE_FACTOR = 1000;
	private static final int MILLISECOND = 1000 * 1000;
	private static final int REPLAY_OFFSET = 2 * 1000 * MILLISECOND;

	CountDownLatch terminationLatch; // NOPMD NOCS (package visible for inner class)
	private final int numWorkers;
	private FSReaderRealtimeCons cons;
	private String constInputPortName;
	private volatile long startTime = -1;
	private volatile long offset = -1;
	private volatile long firstLoggingTimestamp;
	private final ScheduledThreadPoolExecutor executor;
	private long lTime;
	private int active; // no need for voaltile, always within synchronized
	private final int maxQueueSize;
	private AnalysisController controller;

	/**
	 * Constructs a RealtimeReplayDistributor.
	 * 
	 * @param configuration
	 *            The configuration object used to configure this instance.
	 */
	public RealtimeReplayDistributor(final Configuration configuration) {
		super(configuration);

		this.numWorkers = configuration.getIntProperty(CONFIG_PROPERTY_NAME_NUM_WORKERS);
		this.maxQueueSize = this.numWorkers * QUEUE_SIZE_FACTOR;

		this.executor = new ScheduledThreadPoolExecutor(this.numWorkers);
		this.executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(true);
		this.executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
	}

	public void setCons(final FSReaderRealtimeCons cons) {
		synchronized (this) { // to make FindBugs happy
			this.cons = cons;
		}
	}

	public void setConstInputPortName(final String constInputPortName) {
		this.constInputPortName = constInputPortName;
	}

	public void setTerminationLatch(final CountDownLatch terminationLatch) {
		this.terminationLatch = terminationLatch;
	}

	public void setController(final AnalysisController controller) {
		synchronized (this) {
			this.controller = controller;
		}
	}

	@InputPort(name = INPUT_PORT_NAME_MONITORING_RECORDS, eventTypes = { IMonitoringRecord.class })
	public void inputMonitoringRecords(final IMonitoringRecord monitoringRecord) {
		if (this.startTime == -1) { // init on first record
			this.firstLoggingTimestamp = monitoringRecord.getLoggingTimestamp() - (1 * MILLISECOND);
			this.offset = REPLAY_OFFSET - this.firstLoggingTimestamp;
			this.startTime = TIMESOURCE.getTime();
		}
		if (monitoringRecord.getLoggingTimestamp() < this.firstLoggingTimestamp) {
			final MonitoringRecordConsumerException e = new MonitoringRecordConsumerException("Timestamp of current record "
					+ monitoringRecord.getLoggingTimestamp() + " < firstLoggingTimestamp " + this.firstLoggingTimestamp);
			LOG.error("RecordConsumerExecutionException", e);
			return;
		}
		long schedTime = (monitoringRecord.getLoggingTimestamp() + this.offset) // relative to 1st record
				- (TIMESOURCE.getTime() - this.startTime); // substract elapsed time
		if (schedTime < 0) {
			LOG.warn("negative scheduling time: " + schedTime + "-> scheduling with a delay of 0"); // do not append e
			schedTime = 0;
		}
		synchronized (this) {
			while (this.active > this.maxQueueSize) {
				try {
					this.wait();
				} catch (final InterruptedException e) { // ignore
				}
			}
			this.active++;
			final RealtimeReplayWorker worker = new RealtimeReplayWorker(new Configuration());
			worker.initialize(monitoringRecord, this, this.cons, this.constInputPortName, this.controller);
			this.executor.schedule(worker, schedTime, TimeUnit.NANOSECONDS); // *relative*
		}
		this.lTime = this.lTime < monitoringRecord.getLoggingTimestamp() ? monitoringRecord.getLoggingTimestamp() : this.lTime; // NOCS
	}

	public final long getOffset() {
		return this.offset;
	}

	public final long getStartTime() {
		return this.startTime;
	}

	private static final long TERMINATION_DELAY_NANOS = TimeUnit.NANOSECONDS.convert(5, TimeUnit.SECONDS);

	@Override
	public void terminate(final boolean error) {
		final long terminationDelay = TERMINATION_DELAY_NANOS + ((this.lTime + this.offset) - (TIMESOURCE.getTime() - this.startTime)) + (100 * MILLISECOND);
		LOG.info("Will terminate in " + TimeUnit.SECONDS.convert(terminationDelay, TimeUnit.NANOSECONDS) + " secs from now");
		this.executor.schedule(new Runnable() {

			public void run() {
				if (RealtimeReplayDistributor.this.terminationLatch != null) {
					RealtimeReplayDistributor.this.terminationLatch.countDown(); // signal that last record has been scheduled
				} else {
					LOG.warn("terminationLatch == null");
				}
			}
		}, terminationDelay, TimeUnit.NANOSECONDS);
		this.executor.shutdown();
	}

	public void decreaseActive() {
		synchronized (this) {
			this.active--;
			this.notifyAll();
		}
	}

	public int getNumWorkers() {
		return this.numWorkers;
	}

	@Override
	protected Configuration getDefaultConfiguration() {
		// FIXME: Deliver default configuration
		return new Configuration();
	}

	public Configuration getCurrentConfiguration() {
		final Configuration configuration = new Configuration();

		// FIXME: Save the current configuration

		return configuration;
	}
}

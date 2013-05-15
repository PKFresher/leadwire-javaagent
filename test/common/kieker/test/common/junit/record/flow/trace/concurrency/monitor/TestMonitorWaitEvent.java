/***************************************************************************
 * Copyright 2013 Kieker Project (http://kieker-monitoring.net)
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

package kieker.test.common.junit.record.flow.trace.concurrency.monitor;

import org.junit.Assert;
import org.junit.Test;

import kieker.common.record.flow.trace.concurrency.monitor.MonitorWaitEvent;

import kieker.test.common.junit.AbstractKiekerTest;

/**
 * @author Jan Waller
 * 
 * @since 1.8
 */
public class TestMonitorWaitEvent extends AbstractKiekerTest {

	private static final long TSTAMP = 987998L;
	private static final long TRACE_ID = 23444L;
	private static final int ORDER_INDEX = 234;
	private static final int LOCK_ID = 13;

	public TestMonitorWaitEvent() {
		// empty default constructor
	}

	/**
	 * Tests the constructor and toArray(..) methods of {@link MonitorWaitEvent}.
	 * 
	 * Assert that a record instance event1 equals an instance event2 created by serializing event1 to an array event1Array
	 * and using event1Array to construct event2. This ignores a set loggingTimestamp!
	 */
	@Test
	public void testSerializeDeserializeEquals() {

		final MonitorWaitEvent event1 = new MonitorWaitEvent(TSTAMP, TRACE_ID, ORDER_INDEX, LOCK_ID);

		Assert.assertEquals("Unexpected timestamp", TSTAMP, event1.getTimestamp());
		Assert.assertEquals("Unexpected trace ID", TRACE_ID, event1.getTraceId());
		Assert.assertEquals("Unexpected order index", ORDER_INDEX, event1.getOrderIndex());
		Assert.assertEquals("Unexpected lock id", LOCK_ID, event1.getLockId());

		final Object[] event1Array = event1.toArray();

		final MonitorWaitEvent event2 = new MonitorWaitEvent(event1Array);

		Assert.assertEquals(event1, event2);
		Assert.assertEquals(0, event1.compareTo(event2));
	}
}
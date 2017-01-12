/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.monitoring.writer.serializer;

import java.nio.ByteBuffer;
import java.util.Collection;

import kieker.common.record.IMonitoringRecord;

/**
 * Interface for monitoring record serializers.
 *
 * @author Holger Knoche
 *
 * @since 1.13
 */
public interface IMonitoringRecordSerializer {

	/**
	 * Serializes a single record into the given byte buffer.
	 *
	 * @return The number of bytes written to the buffer
	 */
	public int serializeRecord(IMonitoringRecord record, ByteBuffer buffer);

	/**
	 * Serializes multiple monitoring records into the given byte buffer.
	 *
	 * @return The number of bytes written to the buffer
	 */
	public int serializeRecords(Collection<IMonitoringRecord> records, ByteBuffer buffer);

	/**
	 * Implementing classes should indicate an initialization error by throwing an {@link Exception}.
	 */
	public void init() throws Exception;

	/**
	 * Called by the collector to announce a shutdown of monitoring.
	 * Serializers should return as soon as it is safe to terminate Kieker.
	 */
	public void terminate();

}

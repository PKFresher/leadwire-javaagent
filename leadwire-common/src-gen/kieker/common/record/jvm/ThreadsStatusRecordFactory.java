/***************************************************************************
 * Copyright 2017 Kieker Project (http://kieker-monitoring.net)
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
package kieker.common.record.jvm;


import kieker.common.record.factory.IRecordFactory;
import kieker.common.record.io.IValueDeserializer;

/**
 * @author Nils Christian Ehmke
 * 
 * @since 1.10
 */
public final class ThreadsStatusRecordFactory implements IRecordFactory<ThreadsStatusRecord> {
	
	
	@Override
	public ThreadsStatusRecord create(final IValueDeserializer deserializer) {
		return new ThreadsStatusRecord(deserializer);
	}
	
	@Override
	@Deprecated
	public ThreadsStatusRecord create(final Object[] values) {
		return new ThreadsStatusRecord(values);
	}
	
	public int getRecordSizeInBytes() {
		return ThreadsStatusRecord.SIZE;
	}
}

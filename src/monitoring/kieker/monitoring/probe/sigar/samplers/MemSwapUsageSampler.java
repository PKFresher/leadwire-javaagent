/***************************************************************************
 * Copyright 2011 by
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

package kieker.monitoring.probe.sigar.samplers;

import kieker.common.record.system.MemSwapUsageRecord;
import kieker.monitoring.core.controller.IMonitoringController;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.Swap;

/**
 * Logs memory and swap statistics retrieved from {@link Mem} and {@link Swap},
 * as {@link MemSwapUsageRecord}s via {@link kieker.monitoring.core.controller.IMonitoringController#newMonitoringRecord(kieker.common.record.IMonitoringRecord)} .
 * 
 * @author Andre van Hoorn
 * 
 */
public class MemSwapUsageSampler extends AbstractSigarSampler {

	/**
	 * Constructs a new {@link AbstractSigarSampler} with given {@link SigarProxy} instance used to retrieve the sensor data. Users
	 * should use the factory method {@link kieker.monitoring.probe.sigar.SigarSamplerFactory#createSensorMemSwapUsage()} to acquire an
	 * instance rather than calling this constructor directly.
	 * 
	 * @param sigar
	 */
	public MemSwapUsageSampler(final SigarProxy sigar) {
		super(sigar);
	}

	public void sample(final IMonitoringController monitoringController) throws SigarException {
		final Mem mem = this.sigar.getMem();
		final Swap swap = this.sigar.getSwap();
		final MemSwapUsageRecord r = new MemSwapUsageRecord(monitoringController.getTimeSource().getTime(), monitoringController.getHostName(), mem.getTotal(),
				mem.getActualUsed(), mem.getActualFree(), swap.getTotal(), swap.getUsed(), swap.getTotal());
		monitoringController.newMonitoringRecord(r);
	}
}

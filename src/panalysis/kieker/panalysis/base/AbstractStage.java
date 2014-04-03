/***********************************import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
ttp://kieker-monitoring.net)
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

package kieker.panalysis.base;

import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public abstract class AbstractStage<InputPort extends Enum<InputPort>> implements Stage<InputPort> {

	protected int id;
	/**
	 * A unique logger instance per stage instance
	 */
	protected Log logger;

	public AbstractStage() {
		this.logger = LogFactory.getLog(Long.toString(this.id));
	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

}

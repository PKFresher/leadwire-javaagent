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

package kieker.analysisteetime.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author S�ren Henning
 *
 * @since 1.13
 */
public class ObjectIdentifierRegistry {

	private final Map<Object, Integer> identifiers = new HashMap<>();
	private int counter = 0;

	public int getIdentifier(final Object object) {
		final int identifier = this.identifiers.putIfAbsent(object, this.counter);
		this.counter++;
		return identifier;
	}

}
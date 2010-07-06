package kieker.monitoring.core;



/*
 * 
 * Copyright 2006-2008 Matthias Rohr and the Kieker Project 
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
 * 
 * This class provides the method getVersion which returns the version number 
 * set during build (String replacement task within build.xml).
 */

/** @author Andre van Hoorn
 */
public class Version {

    public static final String ANNOTATIONS_PGK="kieker.monitoring.annotations";

	/*
	 * The VERSION string is updated by the Ant build file, which looks for the
	 * pattern: VERSION = <quote>.*<quote>
	 */
	private static final String VERSION = "1.5-trunk-20100706";

	static final String COPYRIGHT = "Copyright (c) 2006-2009 Kieker Project";

    /**
     * Not instantiable.
     */
	private Version()
	{
		super();
	}

    /**
     * Returns the version String.
     *
     * @return the version String.
     */
    
	public static final String getVERSION() {
		return VERSION;
	}
}

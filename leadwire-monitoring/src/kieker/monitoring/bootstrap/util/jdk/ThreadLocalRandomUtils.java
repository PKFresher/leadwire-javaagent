/*
 * Copyright 2016 Naver Corp.
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
 */

package kieker.monitoring.bootstrap.util.jdk;

import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.monitoring.bootstrap.resolver.condition.MainClassCondition;
import kieker.monitoring.common.util.JvmUtils;
import kieker.monitoring.common.util.JvmVersion;

import java.util.Random;

/**
 * @author HyunGil Jeong
 */
public class ThreadLocalRandomUtils {

	static final Log LOG = LogFactory.getLog(MainClassCondition.class); // NOPMD package for inner class

    private static final ThreadLocalRandomFactory THREAD_LOCAL_RANDOM_FACTORY = createThreadLocalRandomFactory();

    // Jdk 7+
    private static final String DEFAULT_THREAD_LOCAL_RANDOM_FACTORY = "kieker.monitoring.bootstrap.util.jdk.JdkThreadLocalRandomFactory";

    private ThreadLocalRandomUtils() {
        throw new IllegalAccessError();
    }

    private static ThreadLocalRandomFactory createThreadLocalRandomFactory() {
        final JvmVersion jvmVersion = JvmUtils.getVersion();
        if (jvmVersion == JvmVersion.JAVA_6) {
            return new PinpointThreadLocalRandomFactory();
        } else if (jvmVersion.onOrAfter(JvmVersion.JAVA_7)) {
            try {
                ClassLoader classLoader = getClassLoader(ThreadLocalRandomUtils.class.getClassLoader());

                final Class<? extends ThreadLocalRandomFactory> threadLocalRandomFactoryClass =
                        (Class<? extends ThreadLocalRandomFactory>) Class.forName(DEFAULT_THREAD_LOCAL_RANDOM_FACTORY, true, classLoader);
                return threadLocalRandomFactoryClass.newInstance();
            } catch (ClassNotFoundException e) {
                logError(e);
            } catch (InstantiationException e) {
                logError(e);
            } catch (IllegalAccessException e) {
                logError(e);
            }
            return new PinpointThreadLocalRandomFactory();
        } else {
            throw new RuntimeException("Unsupported jvm version " + jvmVersion);
        }

    }

    private static ClassLoader getClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            return ClassLoader.getSystemClassLoader();
        }
        return classLoader;
    }

    private static void logError(Exception e) {
        LOG.info("JdkThreadLocalRandomFactory not found.");
    }

    public static Random current() {
        return THREAD_LOCAL_RANDOM_FACTORY.current();
    }
}

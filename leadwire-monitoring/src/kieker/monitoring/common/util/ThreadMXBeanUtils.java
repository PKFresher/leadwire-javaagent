/*
 * Copyright 2014 NAVER Corp.
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

package kieker.monitoring.common.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author emeroad
 */
public final class ThreadMXBeanUtils {

    private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();

    private static final boolean OBJECT_MONITOR_USAGE_SUPPORT;
    private static final boolean SYNCHRONIZER_USAGE_SUPPORT;
    // check support -> getWaitedTime(), getBlockedTime()
    private static final boolean CONTENTION_MONITORING_SUPPORT;

    private static final int DEFAULT_STACK_TRACE_MAX_DEPTH = 32;

    private ThreadMXBeanUtils() {
    }

    static {
        OBJECT_MONITOR_USAGE_SUPPORT = THREAD_MX_BEAN.isObjectMonitorUsageSupported();
        SYNCHRONIZER_USAGE_SUPPORT =  THREAD_MX_BEAN.isSynchronizerUsageSupported();
        CONTENTION_MONITORING_SUPPORT = THREAD_MX_BEAN.isThreadContentionMonitoringSupported();
    }

    // for test
    static String getOption() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ThreadMXBean SupportOption:{OBJECT_MONITOR_USAGE_SUPPORT=");
        builder.append(OBJECT_MONITOR_USAGE_SUPPORT);
        builder.append("}, {SYNCHRONIZER_USAGE_SUPPORT=");
        builder.append(SYNCHRONIZER_USAGE_SUPPORT);
        builder.append("}, {CONTENTION_MONITORING_SUPPORT=");
        builder.append(CONTENTION_MONITORING_SUPPORT);
        builder.append('}');
        return builder.toString();
    }

    public static ThreadInfo[] dumpAllThread() {
//        try {
            return THREAD_MX_BEAN.dumpAllThreads(OBJECT_MONITOR_USAGE_SUPPORT, SYNCHRONIZER_USAGE_SUPPORT);
//        ?? handle exception
//        } catch (java.lang.SecurityException se) {
//            log??
//            return new ThreadInfo[]{};
//        } catch (java.lang.UnsupportedOperationException ue) {
//            log??
//            return new ThreadInfo[]{};
//        }
    }

    public static ThreadInfo findThread(Thread thread) {
        return findThread(thread.getId());
    }

    public static ThreadInfo findThread(Thread thread, int stackTraceMaxDepth) {
        return findThread(thread.getId(), stackTraceMaxDepth);
    }

    public static ThreadInfo findThread(long id) {
        return findThread(id, DEFAULT_STACK_TRACE_MAX_DEPTH);
    }

    public static ThreadInfo findThread(long id, int stackTraceMaxDepth) {
        if (stackTraceMaxDepth <= 0) {
            return THREAD_MX_BEAN.getThreadInfo(id);
        } else {
            return THREAD_MX_BEAN.getThreadInfo(id, stackTraceMaxDepth);
        }
    }

    public static List<ThreadInfo> findThread(String threadName) {
        Asserts.notNull(threadName, "threadName may not be null.");

        ThreadInfo[] threadInfos = dumpAllThread();
        if (threadInfos == null) {
            return Collections.emptyList();
        }

        ArrayList<ThreadInfo> threadInfoList = new ArrayList<ThreadInfo>(1);
        for (ThreadInfo threadInfo : threadInfos) {
            if (threadName.equals(threadInfo.getThreadName())) {
                threadInfoList.add(threadInfo);
            }
        }
        return threadInfoList;
    }

    public static boolean findThreadName(ThreadInfo[] threadInfos, String threadName) {
        if (threadInfos == null) {
            return false;
        }
        for (ThreadInfo threadInfo : threadInfos) {
            if (threadInfo.getThreadName().equals(threadName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean findThreadName(String threadName) {
        final ThreadInfo[] threadInfos = dumpAllThread();
        return findThreadName(threadInfos, threadName);
    }

}

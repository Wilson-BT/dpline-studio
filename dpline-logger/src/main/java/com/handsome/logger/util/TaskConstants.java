/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.handsome.logger.util;


public class TaskConstants {

    private TaskConstants() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * default log cache rows num,output when reach the number
     */
    public static final int DEFAULT_LOG_ROWS_NUM = 4 * 16;

    /**
     * log flush interval?output when reach the interval
     */
    public static final int DEFAULT_LOG_FLUSH_INTERVAL = 1000;

    /**
     * task log info format
     */
    public static final String TASK_LOG_LOGGER_NAME = "TaskLogger";

    /**
     * task log logger name format
     */
    public static final String TASK_LOG_LOGGER_NAME_FORMAT = TASK_LOG_LOGGER_NAME + "-%s";

    /**
     * Task Logger's prefix
     */
    public static final String TASK_LOGGER_INFO_PREFIX = "TASK";

    /**
     * Task Logger Thread's name
     */
    public static final String TASK_APPID_LOG_FORMAT = "taskAppId";

    /**
     * get output log service
     */
    public static final String GET_OUTPUT_LOG_SERVICE = "-getOutputLogService";

    /**
     * date format of yyyyMMdd
     */
    public static final String PARAMETER_FORMAT_DATE = "yyyyMMdd";

    /**
     * date format of yyyyMMddHHmmss
     */
    public static final String PARAMETER_FORMAT_TIME = "yyyyMMddHHmmss";

    /**
     * Task Logger Thread's name
     */
    public static final String TASK_LOGGER_THREAD_NAME = "TaskLogInfo";


}

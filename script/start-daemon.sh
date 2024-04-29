#!/bin/bash

usage="Usage: start-daemon.sh (start|stop|status) <api-server|operator-server>"

# 如果没有参数，show usage
if [ $# -le 1 ]; then
  echo $usage
  exit 1
fi

startStop=$1
shift
command=$1
shift
MAIN_CLASS=$1
shift

echo "Begin $startStop $command......"
JAVA_PATH=`which java`
BIN_DIR=$(cd `dirname $0`;pwd)
DPLINE_HOME=`cd "$BIN_DIR/..";pwd`
DPLINE_CONF=${DPLINE_HOME}/conf
DPLINE_LIB=${DPLINE_HOME}/lib
DPLINE_LOG=${DPLINE_HOME}/logs

if [ ! -d "$DPLINE_LOG" ]; then
  mkdir $DPLINE_LOG
fi
export APP_HOME=$DPLINE_HOME

# 检查应用你是否在运行
function is_exist() {
  PID=$(ps -ef |grep $1 |grep -v grep |awk '{print $2}')
  if [ -z "${PID}" ]; then
    return 1
  else
    return 0
  fi
}
if [ "$command" = "api-server" ]; then
  LOG_BACK=$DPLINE_CONF/logback-api.xml
  ACTIVE_FILE="api"
  JAVA_OPS="-Xms2g -Xmx2g -Xmn1g -XX:+PrintGCDetails -Xloggc:${DPLINE_LOG}/api-gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=dump.hprof"
  MAIN_CLASS="com.dpline.console.ApiApplicationServer"
elif [ "$command" = "operator-server" ]; then
  LOG_BACK=$DPLINE_CONF/logback-operator.xml
  ACTIVE_FILE="operator"
  JAVA_OPS="-Xms2g -Xmx2g -Xmn2g -XX:+PrintGCDetails -Xloggc:${DPLINE_LOG}/operator-gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=dump.hprof"
  MAIN_CLASS="com.dpline.k8s.operator.OperatorServer"
else
  echo "Error: No command named '$command' was found."
  exit 1
fi

case $startStop in
  (start)
    is_exist ${MAIN_CLASS}
    if [ $? -eq "0" ]; then
      echo "$command is already running, PID=${PID}"
    else
      echo "starting $command, logging to ${DPLINE_LOG}"
      echo "nohup ${JAVA_PATH} -server -DAPP_HOME=${DPLINE_HOME} -Duser.timezone=GMT+8 -Dspring.profiles.active=${ACTIVE_FILE} -Dlogging.config=${LOG_BACK} ${JAVA_OPS} -cp ${DPLINE_CONF}:${DPLINE_LIB}/* ${MAIN_CLASS} > /dev/null 2>&1 &"
      nohup ${JAVA_PATH} -server -DAPP_HOME=${DPLINE_HOME} -Duser.timezone=GMT+8 -Dspring.profiles.active=${ACTIVE_FILE} -Dlogging.config=${LOG_BACK} ${JAVA_OPS} -cp ${DPLINE_CONF}:${DPLINE_LIB}/* ${MAIN_CLASS} > /dev/null 2>&1 &
    fi
  ;;
  (stop)
    PID=$(ps -ef |grep ${MAIN_CLASS} |grep -v grep |awk '{print $2}')
    # -z "${pid}"判断pid是否存在，如果不存在返回1，存在返回0
    if [ -n "${PID}" ]; then
      echo "stop $command"
      kill -9 ${PID}
    else
      echo no $command to stop
    fi
  ;;
  (status)
    serverCount=`ps -ef | grep ${MAIN_CLASS} | grep -v grep | wc -l`
    state="STOP"
    state="[ \033[1;31m $state \033[0m ]"
    if [[ $serverCount -gt 0 ]]; then
      state="RUNNING"
      state="[ \033[1;32m $state \033[0m ]"
    fi
    echo -e "$command  $state"
    ;;
  (*)
    echo $usage
    exit 1
    ;;
esac


echo "End $startStop $command."
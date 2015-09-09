DIR=../..
export MAVEN_OPTS="$MAVEN_OPTS -Xmx32G -XX:MaxPermSize=8G -XX:ReservedCodeCacheSize=2G -Dos.arch=ppc64le"
MVN_ARGS="-Pyarn -Phadoop-2.4 -Dhadoop.version=2.4.0 -Pkinesis-asl -Phive-thriftserver -Phive"
export JAVA_TOOL_OPTIONS="-Dos.arch=ppc64le"
export JAVA_OPTS="-Xmx32G -XX:MaxPermSize=8G -XX:ReservedCodeCacheSize=2G -Dos.arch=ppc64le"

#export JAVA_HOME="/usr/lib/jvm/java-8-openjdk-ppc64el"
export JAVA_HOME="/opt/ibm/ibm-java-ppc64le-80SR1FP10"
export PATH="$JAVA_HOME/bin:$PATH"

EXCL_TAGS=\
org.apache.spark.SlowTest,\
org.apache.spark.PPCIBMJDKFailingTest

MVN_COMPILE_PARALLEL_THREADS=20

MVN_CMD="./build/mvn --force"

DBG_PORT=5004

function killZinc() {
    kill `ps aux | grep zinc | grep java | awk '{print $2}'` &&
	echo KILLED ZINC ||
	echo ZINC WAS NOT RUNNING
}

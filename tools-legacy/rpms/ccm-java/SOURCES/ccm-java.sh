. /etc/sysconfig/ccm-java

for jar in "$ORACLE" "$POSTGRES"; do
	case "$CLASSPATH" in
		*"$jar"*) ;;
		"") CLASSPATH=$jar ;;
		*) CLASSPATH=${CLASSPATH}:$jar ;;
	esac
done

export CLASSPATH
export JAVA_HOME

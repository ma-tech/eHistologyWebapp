#! /bin/csh
# build -- Build script for das deployment

# set java variables
setenv JAVA_HOME '/opt/java'
setenv APACHE_ANT_HOME '/opt/apache-ant/lib'

set CP = "$APACHE_ANT_HOME"/ant.jar
set CP = "$CP":"$APACHE_ANT_HOME"/ant-launcher.jar
set CP = "$CP":"$JAVA_HOME"/lib/tools.jar
echo $CP

# to compile IDL
#(cd src; gmake idl; cd ..)

# Execute ANT to perform the requested build target
/opt/java/bin/java -cp $CP org.apache.tools.ant.Main  $*


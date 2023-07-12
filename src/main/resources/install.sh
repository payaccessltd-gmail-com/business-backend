mkdir /opt/pay-access-api
cp /var/lib/jenkins/workspace/PayAccess/target/payaccess-0.0.1-SNAPSHOT-${BUILD_NUMBER}.jar /opt/pay-access-api/payaccess-0.0.1-SNAPSHOT.jar
java -jar /opt/pay-access-api/payaccess-0.0.1-SNAPSHOT.jar
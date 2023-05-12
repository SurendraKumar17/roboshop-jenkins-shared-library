def call() {

    if(!env.SONAR_EXTRA_OPTS) {
       env.SONAR_EXTRA_OPTS = " "
    }

     try {
         node('workstation') {

             stage('Checkout') {
                 cleanWs()
                 git branch: 'main', url: "https://github.com/SurendraKumar17/${component}"
                 sh 'env'
             }

             stage('Compile/Build') {
                 common.compile()
             }

             stage('Unit Tests') {
                 common.unittests()
             }

             stage('Quality Control') {
                 SONAR_PASS = sh(script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.pass  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
                 SONAR_USER = sh(script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.user  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
                 wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${SONAR_PASS}", var: 'SECRET']]]) {
                     sh "sonar-scanner -Dsonar.host.url=http://172.31.3.24:9000 -Dsonar.login=${SONAR_USER} -Dsonar.password=${SONAR_PASS} -Dsonar.projectKey=${component} -Dsonar.qualitygate.wait=true ${SONAR_EXTRA_OPTS}"
                     //sh "echo Sonar Scan"
                 }
             }
         }

        //if (app_lang == "maven") {
            // stage('Build Package') {
             //    sh "mvn package && cp target/${component}-1.0.jar ${component}.jar"
            // }
       //  }

         if (env.PUSH_CODE == "true") {
             stage('Upload Code to Centralized Place') {
                 common.artifactPush()
             }
         }
    } catch(Exception e) {
      common.email("Failed")
      }
}

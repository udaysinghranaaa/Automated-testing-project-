pipeline {
    agent { label 'linux && chrome' }

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    parameters {
        booleanParam(name: 'FULL_REGRESSION', defaultValue: false,
                description: 'Run the original 49-test headless regression')
        booleanParam(name: 'ADVANCED_SUITE', defaultValue: false,
                description: 'Run the isolated advanced Selenium suite')
    }

    options {
        timestamps()
        timeout(time: 60, unit: 'MINUTES')
        disableConcurrentBuilds(abortPrevious: true)
        skipDefaultCheckout(true)
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Environment Verification') {
            steps {
                sh '''
                    java -version
                    mvn -version
                    google-chrome --version
                '''
            }
        }

        stage('Test Compilation') {
            steps {
                sh 'mvn --batch-mode --no-transfer-progress -DskipTests test-compile'
            }
        }

        stage('Headless Smoke') {
            steps {
                sh '''
                    mvn --batch-mode --no-transfer-progress \
                      -Dtestng.suite=testng-parallel-smoke.xml \
                      -Dtestng.output=target/testng-reports/jenkins-smoke \
                      -Dbrowser=chrome -Dheadless=true -Dparallel.threads=3 \
                      exec:java
                '''
            }
        }

        stage('Full Regression') {
            when {
                anyOf {
                    branch 'main'
                    expression { params.FULL_REGRESSION }
                }
            }
            steps {
                sh 'mvn --batch-mode --no-transfer-progress -Dbrowser=chrome -Dheadless=true verify'
            }
        }

        stage('Advanced Selenium') {
            when { expression { params.ADVANCED_SUITE } }
            steps {
                sh '''
                    mvn --batch-mode --no-transfer-progress \
                      -Dtestng.suite=testng-advanced.xml \
                      -Dtestng.output=target/testng-reports/jenkins-advanced \
                      -Dbrowser=chrome -Dheadless=true exec:java
                '''
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                    testResults: 'target/surefire-reports/TEST-*.xml,target/testng-reports/**/junitreports/TEST-*.xml'
            archiveArtifacts allowEmptyArchive: true,
                    artifacts: 'target/surefire-reports/**,target/testng-reports/**,target/shopflow-reports/**,target/shopflow-logs/**',
                    fingerprint: false
            deleteDir()
        }
    }
}

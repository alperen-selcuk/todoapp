pipeline {
  agent {
    kubernetes {
      yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    jenkins: maven-docker
spec:
  containers:
  - name: maven
    image: maven:3.8.5-openjdk-17
    command:
    - cat
    tty: true
    volumeMounts:
    - name: docker-sock
      mountPath: /var/run/docker.sock
  - name: docker
    image: docker:20.10.16
    command:
    - cat
    tty: true
    volumeMounts:
    - name: docker-sock
      mountPath: /var/run/docker.sock
  volumes:
  - name: docker-sock
    hostPath:
      path: /var/run/docker.sock
"""
    }
  }

  stages {
    stage('Maven Build') {
      steps {
        container('maven') {
          sh 'mvn clean install -DskipTests'
        }
      }
    }

    stage('Docker Build') {
      steps {
        container('docker') {
          script {
            // Copy necessary files to the docker container if needed
            sh 'cp -r ../maven/target/ ./'
            
            // Build Docker image using host's Docker daemon
            sh 'docker build -t todoapp:latest .'
          }
        }
      }
    }
  }
}

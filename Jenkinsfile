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
  - name: docker
    image: docker:24.0.5-dind
    securityContext:
      privileged: true
    volumeMounts:
    - name: docker-graph-storage
      mountPath: /var/lib/docker
  volumes:
  - name: docker-graph-storage
    emptyDir: {}
"""
    }
  }

  environment {
    DOCKER_HOST = 'tcp://localhost:2375'
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
          sh '''
            dockerd-entrypoint.sh & sleep 10
            docker build -t todoapp:latest .
          '''
        }
      }
    }
  }
}

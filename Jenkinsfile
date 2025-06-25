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
    image: docker:20.10.16-dind
    securityContext:
      privileged: true
    env:
    - name: DOCKER_HOST
      value: tcp://localhost:2375
    - name: DOCKER_DRIVER
      value: overlay2
    - name: DOCKER_TLS_CERTDIR
      value: ""
  - name: git
    image: alpine/git
    command:
    - cat
    tty: true
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
          withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKERHUB_PASS', usernameVariable: 'DOCKERHUB_USER')]) {
            sh '''
              docker login -u "$DOCKERHUB_USER" -p "$DOCKERHUB_PASS"
              docker build -t  hasanalperen/todoapp:$BUILD_NUMBER .
              docker push hasanalperen/todoapp:$BUILD_NUMBER
            '''
          }
        }
      }
    }
    stage('Kubernetes Deploy') {
      steps {
        container('git') {
          withCredentials([usernamePassword(credentialsId: 'github', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
            sh '''
              git config --global user.email "alperenhasanselcuk@gmail.com"
              git config --global user.name "alperen-selcuk"
              git clone https://$GIT_USER:$GIT_PASS@github.com/alperen-selcuk/todoapp
              cd todoapp
              sed -i 's|hasanalperen/todoapp:.*|hasanalperen/todoapp:'"$BUILD_NUMBER"'|' k8s/deployment.yaml
              git add k8s/deployment.yaml
              git commit -m "CI: update image to hasanalperen/todoapp:$BUILD_NUMBER"
              git push origin main
            '''
          }
      }
    }
}
}
}

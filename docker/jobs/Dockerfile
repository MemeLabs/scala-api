# Pull base image.
FROM centos:centos7

# Install
RUN yum -y update; yum clean all
RUN yum -y install epel-release; yum clean all

# Install sbt
RUN curl https://bintray.com/sbt/rpm/rpm | tee /etc/yum.repos.d/bintray-sbt-rpm.repo
RUN yum -y install sbt; yum clean all

# Install java
RUN yum -y install java-1.8.0-openjdk-devel; yum clean all
ENV JAVA_HOME=/usr/lib/jvm/jre
RUN yum -y update; yum clean all

# Project setup
# -------------

# Add resources
ADD core-jobs/src/main/resources /deploy/resources/

# Add and pack source code
ADD . /tmp/source/app/
WORKDIR /tmp/source/app
RUN sbt core-jobs/pack

# Create app contents area
RUN mkdir -p /deploy/contents
RUN cp -R core-jobs/target/pack/ /deploy/contents

WORKDIR /deploy
EXPOSE 3000

# Start the app
CMD ["/deploy/contents/pack/bin/application"]

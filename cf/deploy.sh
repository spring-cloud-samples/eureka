#!/usr/bin/env bash

set -ev

if ! which cf; then
    wget -O cf/cf.tgz 'https://cli.run.pivotal.io/stable?version=6.2.0&release=linux64-binary'
    (cd cf; tar -zxf cf.tgz)
    export PATH=./cf:$PATH
fi

DOMAIN=${DOMAIN:-23.23.221.131.xip.io}
TARGET=api.${DOMAIN}

cf api | grep ${TARGET} || cf api ${TARGET} --skip-ssl-validation
cf apps | grep OK || cf login -u $CF_USERNAME -p $CF_PASSWORD

function deploy() {

    APP=$1
    APP_HOME=$2

    cf push $APP -p $APP_HOME/target/*.jar --no-start
    cf env $APP | grep SPRING_PROFILES_ACTIVE || cf set-env $APP SPRING_PROFILES_ACTIVE cloud

    if [ "$APP" != "eureka" ]; then
        cf services | grep eureka && cf bind-service $APP eureka
    fi
    if [ "$APP" != "configserver" ]; then
        cf services | grep configserver && cf bind-service $APP configserver
    fi
    
    cf restart $APP
    if ! (cf service-brokers | grep $APP); then
        cf create-service-broker $APP user secure http://$APP.${DOMAIN}
        # TODO push this to server
        for f in `cf curl /v2/service_plans | grep '\"guid' | sed -e 's/.*: "//' -e 's/".*//'`; do 
            cf curl v2/service_plans/$f -X PUT -d '{"public":true}'
        done
    fi

    cf services | grep $APP || cf create-service $APP free $APP

}

deploy $*


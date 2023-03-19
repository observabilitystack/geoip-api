#!/usr/bin/env bats

load /usr/lib/bats/bats-support/load.bash
load /usr/lib/bats/bats-assert/load.bash

@test "[meta] Healthcheck ok" {
  curl -fs "http://geoip-api:8080/actuator/health"
}

@test "[meta] Prometheus metrics ok" {
  curl -fs "http://geoip-api:8080/actuator/prometheus"
}

@test "[meta] Prometheus metrics available" {
  curl -fs "http://geoip-api:8080/actuator/prometheus"|grep "geoip_database"
}

@test "[geoip] 8.8.8.8" {
  run curl -fs "http://geoip-api:8080/8.8.8.8"
  assert_success
  assert_output --partial '"country":"US"'
  assert_output --partial '"asnOrganization":"GOOGLE"'
}

@test "[geoip] 149.233.213.224" {
  run curl -fs "http://geoip-api:8080/149.233.213.224"
  assert_success
  assert_output --partial '"country":"DE"'
  assert_output --partial '"city":"Hamburg"'
  assert_output --partial '"asnOrganization":"wilhelm.tel GmbH"'
}
